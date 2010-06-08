/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */



var provider= [	[["Google","google",,,"https://www.google.com/accounts/o8/id",""],["Yahoo!","yahoo",,,"http://yahoo.com/",""],["AOL","aol","http://openid.aol.com/",,,"America Online/AIM screenname:"]],
				[["Blogger","blogger","http://",".blogspot.com/",,"Google Blogger blog name:"],["Flickr","flickr",,,"http://www.flickr.com/",""],["Livejournal","lj","http://",".livejournal.com/",,"Livejournal username:"]],
				[["myOpenID","myopenid","http://",".myopenid.com/",,"myOpenID username:"],["Verisign","verisign","http://",".pip.verisignlabs.com/",,"Verisign PIP username:"],["Vidoop","vidoop","http://",".myvidoop.com/",,"Vidoop username:"]],
				[["claimID","claimid","http://claimid.com/",,,"claimID username:"],["Technorati","technorati","http://technorati.com/people/technorati/",,,"Technorati username:"],["Vox","vox","http://",".vox.com/",,"Vox username:"]],
				[["Other OpenID","openid",,,"http://",]]
			  ];

var bgcolor = "#ffffff";
var change_color = "#A4D1AA"
var active_color= "#42A44F";

var active_cell=null;
var active_cell_row=null;
var active_cell_col=null;
var username="username";
var openid_input=null;
var input_info="";

function initOpenIDPage(){
openid_input=document.getElementById("openid_identifier");
providerSpace=document.getElementById("provider_space");
username_input="<span style=\"color:green;font-size:1em\">Sign in using;</span><br><div id=\"input_space\" style=\"float: right;\">"+input_info+"<input id =\"input_field\"type=\"text\" size=\"20\" style=\"vertical-align: middle; padding: 2px 2px 2px 20px; background-repeat: no-repeat; background-position: 2px 2px; background-image: url(&quot;images/"+provider[4][0][1]+".ico&quot;);\" onchange=\"enter_username(this);\"onkeyup=\"enter_username(this);\" value=\"username\"></input></div><br>";
provider_table= "<table border=\"0\" style=\"background-color: rgb(224,238,238); width: 100%; border: 0 solid rgb(110, 145, 175);\">";
for(var i =0; i<4;i++){
provider_table+="<tr>";
for(var j =0; j<3;j++){
provider_table+="<td style=\"background-color: rgb(255,255,255);font-weight: bold; padding: 4px; vertical-align: middle; cursor: pointer;\" onclick=\"click(this,"+i+","+j+")\" onmouseover=\"mover(this);\"  onmouseout=\"mout(this);\"><img src=\"images/"+provider[i][j][1]+".ico\" style=\"width: 16px; height: 16px; vertical-align: middle;\">"+provider[i][j][0]+"</td>";
}
provider_table+="</tr>";
}
provider_table+="<tr>\
				<td colspan=\"3\" style=\"background-color: rgb(255,255,255);font-weight: bold; padding: 4px; vertical-align: middle; cursor: pointer;\" onclick=\"click(this,"+4+","+0+")\" onmouseover=\"mover(this);\"  onmouseout=\"mout(this);\"><img src=\"images/"+provider[4][0][1]+".ico\" style=\"width: 16px; height: 16px; vertical-align: middle;\">"+provider[4][0][0]+"</td>\
				</tr>\
				</table>";

providerSpace.innerHTML=username_input+provider_table;
document.getElementById("input_space").style.display = "none";
};

function click(cell,row,col) {
	active_cell_row=row;
	active_cell_col=col;
	
	if (active_cell!==null){
		active_cell.style.backgroundColor = bgcolor;
	}
	
	cell.style.backgroundColor = active_color;
	active_cell=cell;
	
document.getElementById("input_space").innerHTML=provider[active_cell_row][active_cell_col][5]+"<input id =\"input_field\" type=\"text\" size=\"20\" style=\"vertical-align: middle; padding: 2px 2px 2px 20px; background-repeat: no-repeat; background-position: 2px 2px; background-image: url(&quot;images/"+provider[active_cell_row][active_cell_col][1]+".ico&quot;);\" onchange=\"enter_username(this);\"onkeyup=\"enter_username(this);\" value="+username+"></input>";
	if(provider[row][col][4]!=undefined){
		openid_input.value=provider[row][col][4];
		document.getElementById("input_space").style.display = "none";
		document.getElementById("openid_identifier").focus();
	}else {
		formet_input_name(row,col);
		document.getElementById("input_space").style.display = "";	
		document.getElementById("input_field").focus();
    	document.getElementById("input_field").select();	
	}
	

	
};

function formet_input_name(row,col){
		if(provider[row][col][2]!=undefined){
			openid_input.value=provider[row][col][2];
		}
		if(username!=""){
		openid_input.value+=username;
		}else {
		openid_input.value+="username";
		}
		if(provider[row][col][3]!=undefined){
			openid_input.value+=provider[row][col][3];
		}
};

function mover(cell) {
	if(cell!==active_cell){
		cell.style.backgroundColor = change_color;
	}
};

function mout(cell) {
	if(cell!==active_cell){
	 	cell.style.backgroundColor = bgcolor;
	}
};

function enter_username(cell){
	username=cell.value;
    formet_input_name(active_cell_row,active_cell_col);
};