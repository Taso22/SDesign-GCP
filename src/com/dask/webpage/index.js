/**
 *
 */
var map;
var coor;

$(document).ready(function(){
//    var latlng = new google.maps.LatLng(40.82012257801796, -73.94952464052199);
    var latlng = new google.maps.LatLng(40.821232, -73.947983);

    var myOptions = {
        zoom: 17,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

    init_path();

    google.maps.event.addListener(map, "click", function(event) {
//        alert(event.latLng.toString());
        $("#output_js").append("\nusr.push(new google.maps.LatLng" + 
        		event.latLng.toString() + ");");
        $("#output_jv").append("\n{" + 
        		event.latLng.toString().replace(/\(|\)/, "") + "},");
		var marker = new google.maps.Marker({
			position: event.latLng,
			icon: "http://www.cespage.com/silverlight/appbar/dark/person.png",
			title: event.latLng.toString(),
			map: map
		});
	});

});

function init_path() {
	var coor = new Array();

//	/* Route to Subway from ST */
//	coor.push(new google.maps.LatLng(40.821232, -73.947983));
//	coor.push(new google.maps.LatLng(40.821491, -73.94854));
//	coor.push(new google.maps.LatLng(40.81879, -73.950508));
//	coor.push(new google.maps.LatLng(40.818771, -73.950523));
//	coor.push(new google.maps.LatLng(40.81868, -73.950577));
//	coor.push(new google.maps.LatLng(40.818569, -73.950684));
//	coor.push(new google.maps.LatLng(40.818481, -73.950783));
//	coor.push(new google.maps.LatLng(40.81839, -73.950882));
//	coor.push(new google.maps.LatLng(40.818909, -73.952209));
//	coor.push(new google.maps.LatLng(40.818989, -73.952408));

	/* Route to Burger King from ST */
	coor.push(new google.maps.LatLng(40.821232, -73.947983));
	coor.push(new google.maps.LatLng(40.821491, -73.94854));
	coor.push(new google.maps.LatLng(40.82206, -73.949913));
	coor.push(new google.maps.LatLng(40.822651, -73.951302));
	coor.push(new google.maps.LatLng(40.823238, -73.952713));
	coor.push(new google.maps.LatLng(40.823318, -73.952904));
	coor.push(new google.maps.LatLng(40.823689, -73.952629));

	
	
    var black = "#333333";
    var blue = "#1e90ff";
    var color= "";
    for(var i=0; i<coor.length-1; i++) {
	    if(i%2 == 0)
	        color = black;
        else
            color = blue;

        var temp = new Array(coor[i], coor[i+1]);

        var path = new google.maps.Polyline({
            path: temp,
            strokeColor:color,
            strokeOpacity: 2.0,
            strokeWeight: 4
        });
        path.setMap(map);
    }
    
    var usr = Array();
    
    usr.push(new google.maps.LatLng(40.82140047064303, -73.94782474966814));
    usr.push(new google.maps.LatLng(40.821274625142884, -73.94795617790987));
    usr.push(new google.maps.LatLng(40.82138220276265, -73.9481948945122)); 
    usr.push(new google.maps.LatLng(40.82149992901453, -73.9484470221596)); 
    usr.push(new google.maps.LatLng(40.82154255398522, -73.9485543105202)); 
    usr.push(new google.maps.LatLng(40.82183483876072, -73.94921949835589)); 
    usr.push(new google.maps.LatLng(40.82208043816645, -73.94982031317522)); 
    usr.push(new google.maps.LatLng(40.822157568370706, -73.94996783467104)); 
    usr.push(new google.maps.LatLng(40.82242143418061, -73.9506276580887)); 
    usr.push(new google.maps.LatLng(40.82266906113967, -73.9512016508179)); 
    usr.push(new google.maps.LatLng(40.822758368996425, -73.9513652655678)); 
    usr.push(new google.maps.LatLng(40.82300193526726, -73.95199022026827)); 
    usr.push(new google.maps.LatLng(40.82325158976658, -73.95255884857943)); 
    usr.push(new google.maps.LatLng(40.82333074831405, -73.95271978112032)); 
    usr.push(new google.maps.LatLng(40.823413966172325, -73.95291021796038)); 
    usr.push(new google.maps.LatLng(40.82366970820524, -73.95270905228426));
    
    color= "#F00";
    for(var i=0; i<usr.length-1; i++) {
    	var temp = new Array(usr[i], usr[i+1]);

        var path = new google.maps.Polyline({
            path: temp,
            strokeColor:color,
            strokeOpacity: 2.0,
            strokeWeight: 4
        });
        path.setMap(map);
    }
    
}
