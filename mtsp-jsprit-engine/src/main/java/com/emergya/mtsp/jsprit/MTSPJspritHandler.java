package com.emergya.mtsp.jsprit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import jsprit.analysis.toolbox.StopWatch;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.io.VehicleRoutingAlgorithms;
import jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListeners.Priority;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.activity.TourActivities;
import jsprit.core.problem.solution.route.activity.TourActivity;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.core.reporting.SolutionPrinter.Print;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;

import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.emergya.mtsp.model.Stop;
import com.emergya.mtsp.model.Vehicle;
import com.emergya.mtsp.model.Way;
import com.emergya.mtsp.response.MTSPRoute;
import com.emergya.mtsp.response.MTSPRoutes;
import com.emergya.mtsp.util.MTSPUtil;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;
import jsprit.core.problem.cost.TransportTime;
import jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import jsprit.core.problem.driver.Driver;

/**
 *
 * @author marcos
 */
@Component
public class MTSPJspritHandler {

    final int WEIGHT_INDEX = 0;

    @Value("${smc.mtsp.osmFilePath}")
    private String OSM_FILE_PATH;
    @Value("${smc.mtsp.graphPath}")
    private String GRAPH_PATH;
    @Value("${smc.mtsp.vehicle}")
    private String VEHICLE;
    @Value("${smc.mtsp.weighting}")
    private String WEIGHTING;

    private GraphHopper hopper;

    public MTSPRoutes calculateMTSP(final List<Vehicle> vehicles, final List<Stop> stops) {
        MTSPRoutes res = new MTSPRoutes();

        List<VehicleImpl> vehicle_list = getVehicleList(vehicles);
        List<Service> services = getServiceList(stops, WEIGHT_INDEX);
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        for (VehicleImpl v : vehicle_list) {
            vrpBuilder.addVehicle(v);
        }

        for (Service service : services) {
            vrpBuilder.addJob(service);

        }

//		MTSPRoutingCosts mtspcosts = new MTSPRoutingCosts(hopper);
        //vrpBuilder.setRoutingCost(mtspcosts);
        vrpBuilder.setFleetSize(FleetSize.FINITE);

        VehicleRoutingProblem problem = vrpBuilder.build();

        /*
         * solve the problem
         */
        VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(problem, "com/emergya/mtsp/resource/algorithmConfig.xml");

        vra.getAlgorithmListeners().addListener(new StopWatch(), Priority.HIGH);
        vra.getSearchStrategyManager();
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        List<VehicleRoute> routes = (List<VehicleRoute>) bestSolution.getRoutes();
        GHPoint endPoint = null;
        for (VehicleRoute r : routes) {
            String id = r.getVehicle().getId();
            String originId = null;
            String targetId = null;
            Double costDistance = null;
            Double costTime = null;
            for (Vehicle v : vehicles) {
                if (v.getId().equals(id)) {
                    originId = v.getOrigin().getId();
                    targetId = v.getTarget().getId();
                    costDistance = v.getCostPerDistance();
                    costTime = v.getCostPerTime();
                }
            }
            Stop origin = new Stop(id, "origin_" + id, r.getVehicle().getStartLocationCoordinate().getY(), r.getVehicle().getStartLocationCoordinate().getX());
            Stop target = new Stop(r.getVehicle().getId(), "target_" + r.getVehicle().getId(), r.getVehicle().getEndLocationCoordinate().getY(), r.getVehicle().getEndLocationCoordinate().getX());
            MTSPRoute route = new MTSPRoute(new Vehicle(r.getVehicle().getId(), r.getVehicle().getType().getTypeId(), origin, target, costDistance, costTime));
            TourActivities activities = r.getTourActivities();

            Iterator<TourActivity> it = activities.iterator();
            List<GHPoint> points = new LinkedList<GHPoint>();
            // Start point
            GHPoint startPoint = MTSPUtil.getCoordinatesFromString(r.getStart().getLocationId());
            points.add(startPoint);
            // End point
            endPoint = MTSPUtil.getCoordinatesFromString(r.getEnd().getLocationId());
            //points.add(endPoint);
            // Intermediate points
            List<Way> ways = new LinkedList<Way>();

            int i = 0;
            while (it.hasNext() && i < r.getActivities().size() - 1) {
                // Get the coordinates string

                TourActivity ta = r.getActivities().get(i);
                TourActivity taNext = r.getActivities().get(i + 1);

                String index = ta.getLocationId();
                String indexNext = taNext.getLocationId();
                // Get Coordinates point

                GHPoint point = MTSPUtil.getCoordinatesFromString(index);
                GHPoint pointNext = MTSPUtil.getCoordinatesFromString(indexNext);
                points.add(point);

               
                TourActivity.JobActivity job = (TourActivity.JobActivity) ta;
                TourActivity.JobActivity jobNext = (TourActivity.JobActivity) taNext;
                
                if ((!originId.equals(job.getJob().getId())) && (i == 0)) {
                    Way wayIni = calculateWay(startPoint, point, originId, job.getJob().getId(), bestSolution.getCost());
                    ways.add(wayIni);
                }
                Way wayTa = calculateWay(point, pointNext, job.getJob().getId(), jobNext.getJob().getId(), bestSolution.getCost());
                ways.add(wayTa);
                

                if ((!targetId.equals(jobNext.getJob().getId())) && (i == r.getActivities().size() - 2)) {
                   Way wayFinal = calculateWay(pointNext, endPoint, jobNext.getJob().getId(), targetId, bestSolution.getCost());
                   ways.add(wayFinal);
                }
 
                i++;
                it.next();
            }

             //only one job assigned to vehicle
            if (ways.size() == 0 && r.getActivities().size()!= 0) {
                Way way = calculateWay(startPoint, endPoint, originId, targetId, bestSolution.getCost());
                ways.add(way);
            }

            GHRequest request = new GHRequest(points).setVehicle(this.VEHICLE).setWeighting(this.WEIGHTING);

            GHResponse response = hopper.route(request);

            PointList result_points = response.getPoints();

            GHRequest last_request = new GHRequest(points.get(points.size() - 1).getLat(), points.get(points.size() - 1).getLon(), endPoint.getLat(), endPoint.getLon());
            GHResponse last_response = hopper.route(last_request);
            PointList last_result_points = last_response.getPoints();
            // Get geometry linestring
            LineString geom = getRoute(result_points);
            LineString last_geom = getRoute(last_result_points);
            for (LngLatAlt el : last_geom.getCoordinates()) {
                geom.add(el);
            }
            route.setGeometry(geom);

            route.setWays(ways);
            res.getRoutes().add(route);
        }

        //SolutionPrinter.print(problem,bestSolution,Print.VERBOSE);
        return res;
    }

    private LineString getRoute(PointList points) {
        LineString route = new LineString();
        for (int p = 0; p < points.getSize(); p++) {
            Double latitude = points.getLatitude(p);
            Double longitude = points.getLongitude(p);
            route.add(new LngLatAlt(longitude, latitude));
        }
        return route;
    }

    private GHResponse calculateRoute(GHPoint startPoint, GHPoint endPoint) {
        GHRequest request = new GHRequest(startPoint.getLat(), startPoint.getLon(), endPoint.getLat(), endPoint.getLon()).setVehicle(this.VEHICLE).setWeighting(this.WEIGHTING);
        GHResponse response = hopper.route(request);
        return response;
    }

    private Way calculateWay(GHPoint startPoint,GHPoint endPoint, String idStart, String idEnd, Double cost) {
        GHResponse response = calculateRoute(startPoint, endPoint);
        PointList result_points = response.getPoints();
        // Get geometry linestring
        LineString geom = getRoute(result_points);

        Way way = new Way(idStart, idEnd, geom, response.getMillis(), response.getDistance(), cost);
        return way;
    }

    private List<Service> getServiceList(List<Stop> stops, int wEIGHT_INDEX2) {
        List<Service> services = new LinkedList<Service>();

        for (int i = 0; i < stops.size(); i++) {
            Service service = Service.Builder.newInstance(stops.get(i).getId()).addSizeDimension(WEIGHT_INDEX, 1).setCoord(Coordinate.newInstance(stops.get(i).getLongitude(), stops.get(i).getLatitude())).setName(stops.get(i).getName()).build();

            services.add(service);
        }

        return services;
    }

    private List<VehicleImpl> getVehicleList(List<Vehicle> vehicles) {
        List<VehicleImpl> vehicles_list = new LinkedList<VehicleImpl>();

        int i = 0;
        for (Vehicle s : vehicles) {
            VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance(s.getName()).addCapacityDimension(WEIGHT_INDEX, 100).setCostPerDistance(s.getCostPerDistance()).setCostPerTime(s.getCostPerTime());
            VehicleType vehicleType = vehicleTypeBuilder.build();
            VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(s.getId());
            vehicleBuilder.setLatestArrival(10);
            Stop origin = s.getOrigin();
            Stop target = s.getTarget();
            vehicleBuilder.setStartLocationCoordinate(Coordinate.newInstance(origin.getLongitude(), origin.getLatitude()));
            vehicleBuilder.setEndLocationCoordinate(Coordinate.newInstance(target.getLongitude(), target.getLatitude()));
            vehicleBuilder.setType(vehicleType);
            VehicleImpl vehicle = vehicleBuilder.build();
            vehicles_list.add(vehicle);
            i++;
        };

        return vehicles_list;
    }

    @PostConstruct
    public void init() {
        hopper = new GraphHopper().forServer();
        hopper.setInMemory(true);
        hopper.disableCHShortcuts();
        hopper.setOSMFile(this.OSM_FILE_PATH);
        hopper.setGraphHopperLocation(this.GRAPH_PATH);
        hopper.setEncodingManager(new EncodingManager(this.VEHICLE));

        hopper.importOrLoad();
    }

}
