/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var mtsp = {
    originList: [],
    targetList: [],
    map: null,
    routesGroup: null,
    initMap: function () {

        var map = L.map('map').setView([37.389254195966004, -5.977935791015625], 13);

        mtsp.routesGroup = L.featureGroup([]);
        mtsp.routesGroup.addTo(map);


        L.tileLayer('http://{s}.tile.openstreetmap.se/hydda/full/{z}/{x}/{y}.png', {
        	minZoom: 0,
        	maxZoom: 18,
        	attribution: 'Tiles courtesy of <a href="http://openstreetmap.se/" target="_blank">OpenStreetMap Sweden</a> &mdash; Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>'
        }).addTo(map);
        
        L.easyButton('fa-map-marker green', mtsp.activateOrigins,'Origins routes', map);
        L.easyButton('fa-map-marker red', mtsp.activateTargets,'Targets routes', map);
        L.easyButton('fa-play-circle', mtsp.calculateRoutes,'', map);
        
        mtsp.map = map;

    },
    
    activateOrigins: function(e){
    	mtsp.map.off("click", mtsp.onClickTargets);
    	mtsp.map.on("click", mtsp.onClickOrigins);
    },
    
    activateTargets: function(e){
    	mtsp.map.off("click", mtsp.onClickOrigins);
    	mtsp.map.on("click", mtsp.onClickTargets);
    },
    
    onClickOrigins: function(e){
    	mtsp.originList.push({
            name: "Stop",
            latitude: e.latlng.lat,
            longitude: e.latlng.lng
        });
    	
    	var icon = L.icon({
    		iconUrl:'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|00FF00',
    		iconSize: [21, 34],
    		iconAnchor: [10, 40]
    	});

        L.marker([e.latlng.lat, e.latlng.lng], {icon: icon}).addTo(mtsp.map);

        $("#stopList").append("<li>" + e.latlng.lat + ", " + e.latlng.lng + "</li>");
    },
    
    onClickTargets: function(e){
    	mtsp.targetList.push({
            name: "Stop",
            latitude: e.latlng.lat,
            longitude: e.latlng.lng
        });
    	
    	var icon = L.icon({
    		iconUrl:'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|FF0000',
    		iconSize: [21, 34],
    		iconAnchor: [10, 40]
    	});

        L.marker([e.latlng.lat, e.latlng.lng], {icon: icon}).addTo(mtsp.map);

        $("#stopList").append("<li>" + e.latlng.lat + ", " + e.latlng.lng + "</li>");
    },
    
    calculateRoutes: function () {
        mtsp.routesGroup.clearLayers();
        $.ajax({
            method: "POST",
            url: "v1/calculateMTSP",
            data: JSON.stringify({origins: mtsp.originList, stops: mtsp.targetList}),
            success: function (data) {
                console.debug(data);

                var colors = ["red","blue","green"];

                for (var i = 0; i < data.routes.length; i++) {
                    L.geoJson(data.routes[i].feature,{
                        style: function() {
                            return {color: colors[i]};
                        }
                    }).addTo(mtsp.routesGroup);
                }
            },
            dataType: "json",
            contentType: "application/json"
        });
    }
};


window.onload = mtsp.initMap;