var accessManager;
var roles;
var groups;
//var selectedGroup = "New Group";
//var roleIndex = 0;
var users;
var currentGroupUsers = ",";
var currentGroup = -1;
var currentRoleGroup = -1;
var currentRole = -1;
var currentAlbumPermissions = new Array();

var currentSuperAdminRole = -1;
var allPermissions;

var blockedUsers = new Array();


function populateRoleAlbums() {
    gallery = new dojo.rpc.JsonService(photark.constants.GalleryServiceEndpoint);
    gallery.getAlbumsToUser(SECURITY_TOKEN).addCallback(function(albums, exception) {
        if (exception) {
            adminLogout();
        }
        this.albums = albums;
        var selectAlbum = dojo.byId("selectRoleAlbum");
        selectAlbum.options.length = 0;

        selectAlbum.options[ selectAlbum.options.length] = new Option("-Select Album-", "-Select Album-", true, false);
        for (var pos = 0; pos < albums.length; pos++) {
            if ((albums[pos].owners + "").indexOf(userId) != -1) {
                selectAlbum.options[selectAlbum.options.length] = new Option(albums[pos].name, albums[pos].name, false, false);
            }
        }

        dojo.byId("selectRoleAlbum").value = albumName;

        populateRoles();
        populateRoleGroups();
        manageRoleAlbumFields();

    });
}

function populateRoles() {
    //  if (roles==undefined){
    accessManager = new dojo.rpc.JsonService(photark.constants.AccessManagerServiceEndpoint);
    accessManager.getRoles().addCallback(function(roles, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        this.roles = roles;
        var addRole = dojo.byId("addRole");

        addRole.options.length = 0;
        //        if (permissions.indexOf("|createGroupRole|") != -1) {
        addRole.options[addRole.options.length] = new Option("-Select Role-", -1, true, false);
        //        }
        for (var pos = 0; pos < roles.length; pos++) {
            addRole.options[addRole.options.length] = new Option(roles[pos].roleName, pos, false, false);
        }
        dojo.byId("addRole").value = currentRole;
        manageRoleFields();
        // showRoleInfo();

    });
    //   }
}
function populateRoleGroups() {
    accessManager = new dojo.rpc.JsonService(photark.constants.AccessManagerServiceEndpoint);
    accessManager.getGroups(SECURITY_TOKEN).addCallback(function(groups, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        this.groups = groups;
        var addRoleGroup = dojo.byId("addRoleGroup");

        addRoleGroup.options.length = 0;
        //if (permissions.indexOf("|createGroupRole|") != -1) {
        addRoleGroup.options[ addRoleGroup.options.length] = new Option("-Select User Group-", -1, true, false);
        /// }
        for (var pos = 0; pos < groups.length; pos++) {
            addRoleGroup.options[addRoleGroup.options.length] = new Option(groups[pos][0], pos, false, false);

        }
        dojo.byId("addRoleGroup").value = currentRoleGroup;

        manageRoleGroupFields();
    });
}


function manageRoleAlbumFields() {
    if (dojo.byId("selectRoleAlbum").value != "-Select Album-") {
        var albumName = dojo.byId("selectRoleAlbum").value;
        accessManager.getAlbumPermissionInfo(albumName, SECURITY_TOKEN).addCallback(function(values, exception) {
            if (exception) {
                //            alert(exception.msg);
                //            return;
                adminLogout();
            }
            currentAlbumPermissions = values;


            showPermissionInfo();
        });
    } else {
        document.getElementById('roleTableDiv').innerHTML = "<table id='roleTable'  border='0' ></table>";
    }

    manageAssignRoleButton();
}
function manageRoleFields() {
    currentRole = dojo.byId("addRole").value;
    showRoleInfo();
    manageAssignRoleButton();
}
function manageRoleGroupFields() {
    currentRoleGroup = dojo.byId("addRoleGroup").value;
    manageAssignRoleButton();
}


function manageAssignRoleButton() {
    var role = dojo.byId("addRole").value;

    if (dojo.byId("selectRoleAlbum").value == "-Select Album-") {


        dojo.byId("roleLine").style.display = "none";
        dojo.byId("roleInfoTableDiv").style.display = "none";
        dojo.byId("assignRoleButtonDiv").style.display = "none";
        dojo.byId("addRoleGroupDiv").style.display = "none";

        dojo.byId("addRoleDiv").style.display = "none";
        dojo.byId("saveRole").style.display = "none";


    } else {
        dojo.byId("roleLine").style.display = "";
        dojo.byId("addRoleDiv").style.display = "";
        dojo.byId("saveRole").style.display = "";
        if (role == -1) {
            dojo.byId("assignRoleButtonDiv").style.display = "none";
            dojo.byId("addRoleGroupDiv").style.display = "none";
            dojo.byId("roleInfoTableDiv").style.display = "none";


        } else if (roles[role].roleName == "allUsersViewRole") {
            dojo.byId("assignRoleButtonDiv").style.display = "";

            dojo.byId("addRoleGroupDiv").style.display = "none";
            dojo.byId("roleInfoTableDiv").style.display = "";
        } else {
            dojo.byId("roleInfoTableDiv").style.display = "";
            dojo.byId("addRoleGroupDiv").style.display = "";
            if (currentRoleGroup == -1) {
                dojo.byId("assignRoleButtonDiv").style.display = "none";
            } else {
                dojo.byId("assignRoleButtonDiv").style.display = "";
            }
        }
    }

}
function showPermissionInfo() {
    var table = "<table border=\"1\" style=\"width:60%;\">\n" +
            "<tr>\n" +
            "<td><b>Roles</b></td>\n" +
            " <td ><b>Assigned User Groups</b></td>" +
            "</tr>";
    for (var i = 0; i < currentAlbumPermissions.length; i++) {
        var userGpInfo = "<table border=\"0\" >\n";
        if (currentAlbumPermissions[i][0] == "allUsersViewRole") {
            userGpInfo += "<tr>\n" +
                    "<td><label >All Users</label></td>\n" +
                    "<td><a href=\"javascript:confirmRemoveGroup('" + currentAlbumPermissions[i][0] + "','');\">Remove</a></td>" +
                    "</tr>";
        } else {
            if(currentAlbumPermissions[i][1].replace(" ")==""){
                continue;
            }
            var userGroups = currentAlbumPermissions[i][1].split(",");
            for (var j = 0; j < userGroups.length; j++) {
                userGpInfo += "<tr>\n" +
                        "<td><label >" + userGroups[j] + "</label></td>\n" +
                        "<td><a href=\"javascript:confirmRemoveGroup('" + currentAlbumPermissions[i][0] + "','" + userGroups[j] + "');\">Remove</a></td>" +
                        "</tr>";
            }
        }
        userGpInfo += "  </table>";
        table += "<tr>\n" +
                "<td><label >" + currentAlbumPermissions[i][0] + "</label></td>\n" +
                "<td>" + userGpInfo + "</td>\n" +
                "</tr>";
    }
    table += "  </table>";
    document.getElementById('roleTableDiv').innerHTML = "<hr align=left width=60% noshade>" + table;
}


function confirmRemoveGroup(roleName, userGroupName) {
    for (var i = 0; i < currentAlbumPermissions.length; i++) {
        if (currentAlbumPermissions[i][0] == roleName) {
            if (currentAlbumPermissions[i][0] != "allUsersViewRole" && currentAlbumPermissions[i][1] != userGroupName) {
                currentAlbumPermissions[i][1] = (currentAlbumPermissions[i][1] + ",").replace(userGroupName + ",", "");
                currentAlbumPermissions[i][1] = currentAlbumPermissions[i][1].substring(0, currentAlbumPermissions[i][1].length - 1);
                return;
            }
            var newAlbumPermissions = new Array();
            var k = 0;
            for (var j = 0; j < currentAlbumPermissions.length; j++) {
                if (j != i) {
                    newAlbumPermissions[k] = currentAlbumPermissions[j];
                    k++;
                }
            }
            currentAlbumPermissions = newAlbumPermissions;
        }
    }
    showPermissionInfo();
}

function assignRole() {
    var roleExist = false;
    var i = 0;
    for (; currentAlbumPermissions.length > i; i++) {
        if (currentAlbumPermissions[i][0] == roles[currentRole].roleName) {
            if ("allUsersViewRole" == roles[currentRole].roleName) {
                roleExist = true;
                break;
            }
            var userGroups = currentAlbumPermissions[i][1];
            if ((userGroups + ",").indexOf(groups[currentRoleGroup][0] + ",") != -1) {
                alert(groups[currentRoleGroup][0] + " is already added to the role " + roles[currentRole].roleName + "!");
                roleExist = true;
                break;
            } else {
                if (userGroups != undefined && userGroups != null && userGroups != "") {
                    userGroups += "," + groups[currentRoleGroup][0];
                } else {
                    userGroups = groups[currentRoleGroup][0];
                }
                currentAlbumPermissions[i] = new Array(roles[currentRole].roleName, userGroups);
                roleExist = true;
                break;
            }
        }
    }
    if (! roleExist) {
        if ("allUsersViewRole" == roles[currentRole].roleName) {
            currentAlbumPermissions[i] = new Array(roles[currentRole].roleName, null);
        } else {
            currentAlbumPermissions[i] = new Array(roles[currentRole].roleName, groups[currentRoleGroup][0]);
        }
    }
    dojo.byId("addRoleGroup").value = -1;
    dojo.byId("addRole").value = -1;
    manageRoleFields();
    manageRoleGroupFields();
    showPermissionInfo();
}

function saveRoleInfo() {
    if (confirm("Are you sure you want to save the changes ?")) {
        var album = dojo.byId("selectRoleAlbum").value;
        if (album == "-Select Album-") {
            alert("Select an album before saving!");
        } else {
            accessManager.addToRole(album, currentAlbumPermissions, SECURITY_TOKEN).addCallback(function(values, exception) {
                if (exception) {
                    //            alert(exception.msg);
                    //            return;
                    adminLogout();
                }
                showPermissionInfo();
                alert(album + " permissions updated!");
            });
        }
    }
}

function showRoleInfo() {
    var addRole = dojo.byId("addRole");
    currentRole = addRole.value;
    var extraText = "";
    if (currentRole == -1) {
        document.getElementById('roleInfoTableDiv').innerHTML = "<table id='roleInfoTable'  border='0' ></table>";
    } else {
        var table = "<table id='roleInfoTable' border=\"0\" style=\"width:60%;\">\n";
        if (roles[currentRole].roleName == "allUsersViewRole") {
            extraText = " for All Users"
            table += "<tr>\n" +
                    "<td><label ><b>" + roles[currentRole].permissions[0].permission + " :</b></label></td>\n" +
                    "<td><label >Allow <b>all</b> the users to view the album images</label></td>\n" +
                    "</tr>";
        } else {
            for (var i = 0; i < roles[currentRole].permissions.length; i++) {
                table += "<tr>\n" +
                        "<td><label ><b>" + roles[currentRole].permissions[i].permission + " :</b></label></td>\n" +
                        "<td><label >" + roles[currentRole].permissions[i].permissionDesc + extraText + "</label></td>\n" +
                        "</tr>";
            }
        }
        table += "  </table>";
        document.getElementById('roleInfoTableDiv').innerHTML = table;
    }
}

function manageGroupFields() {
    currentGroup = dojo.byId("selectGroup").value;
    currentGroupUsers = ",";
    if (currentGroup == -1) {
        dojo.byId("newGroupName").style.display = "";
        dojo.byId("newGroupLabel").style.display = "";
        dojo.byId("deleteGroup").style.display = "none";
    } else {
        dojo.byId("newGroupName").style.display = "none";
        dojo.byId("newGroupLabel").style.display = "none";
        dojo.byId("deleteGroup").style.display = "";
        for (var i = 0; i < groups[currentGroup][1].length; i++) {
            for (var j = 2; j < users.length; j++) {
                if (groups[currentGroup][1][i] == users[j].userId && users[j].userId != userId) {
                    currentGroupUsers += j + ",";
                }
            }
        }
    }
    generateGroupUserTable();
}

//******************************************************************************************************************8
//user groups

function populateGroups() {
    accessManager = new dojo.rpc.JsonService(photark.constants.AccessManagerServiceEndpoint);
    accessManager.getGroups(SECURITY_TOKEN).addCallback(function(groups, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        this.groups = groups;
        var selectGroup = dojo.byId("selectGroup");
        selectGroup.options.length = 0;
        //if (permissions.indexOf("|createGroupRole|") != -1) {
        selectGroup.options[ selectGroup.options.length] = new Option("New Group", -1, true, false);
        /// }
        for (var pos = 0; pos < groups.length; pos++) {
            selectGroup.options[selectGroup.options.length] = new Option(groups[pos][0], pos, false, false);
        }
        dojo.byId("selectGroup").value = currentGroup;
        populateGroupUsers();
        manageGroupFields();
    });
}


function generateGroupUserTable() {
    var userNos = currentGroupUsers.split(",");
    var table = "<table border=\"1\" style=\"width:60%;\">";
    for (var i = 1; i < userNos.length - 1; i++) {
        table += "<tr>\n" +
                "<td><label >" + users[userNos[i]].userInfo.displayName + "</label></td>\n" +
                "<td><a href=\"javascript:confirmRemoveUser('" + users[userNos[i]].userInfo.displayName + "'," + userNos[i] + ");\">Remove</a></td>" +
                "</tr>";
    }
    table += "  </table>";
    document.getElementById('userTableDiv').innerHTML = table;
}

function confirmRemoveUser(userName, userNo) {
    // if (confirm("Are you sure you want to remove " + userName + "?")) {   // not implemented for now
    currentGroupUsers = currentGroupUsers.replace(userNo + ",", "");
    generateGroupUserTable();
    // }

}
function populateGroupUsers() {
    accessManager = new dojo.rpc.JsonService(photark.constants.AccessManagerServiceEndpoint);
    accessManager.getAllUsers().addCallback(function(users, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        this.users = users;
        var addUser = dojo.byId("addUser");
        addUser.options.length = 0;
        //        if (permissions.indexOf("|createGroupRole|") != -1) {
        addUser.options[addUser.options.length] = new Option("-Select User-", "-Select User-", true, false);
        //        }
        for (var pos = 2; pos < users.length; pos++) {
            if (users[pos].userId != userId) {
                addUser.options[addUser.options.length] = new Option(users[pos].userInfo.displayName, pos, false, false);
            }
        }
        dojo.byId("addUser").value = "-Select User-";
        dojo.byId("addUserButton").style.display = "none";
        //        showRoleInfo();
        manageGroupUserFields();
    });
}

function manageGroupUserFields() {
    if (dojo.byId("addUser").value != "-Select User-") {
        dojo.byId("addUserButton").style.display = "";
    } else {
        dojo.byId("addUserButton").style.display = "none";
    }
}


function addGroupUser() {
    if (dojo.byId("addUser").value != "-Select User-") {
        if (currentGroupUsers.indexOf(dojo.byId("addUser").value + ",") == -1) {
            currentGroupUsers += dojo.byId("addUser").value + ",";
            generateGroupUserTable();
        } else {
            alert(users[dojo.byId("addUser").value].userInfo.displayName + " allready added!");
        }
        dojo.byId("addUser").value = "-Select User-";
        manageGroupUserFields();
    }
}

function saveGroup() {
    if (confirm("Are you sure you want to save the changes ?")) {
        if (currentGroupUsers == ",") {
            alert("Please add users before saving!");
            return;
        } else {
            var currentGroupName;
            if (currentGroup == -1) {
                var name = dojo.byId("newGroupName").value;
                dojo.byId("newGroupName").value = "";
                if (name == null || (name != null && name == "" && name.length == 0)) {
                    alert("Group cannot be created. Enter a group name!");
                    return;
                }
                currentGroup = dojo.byId("selectGroup").options.length - 1;
                currentGroupName = name;
            } else {
                currentGroupName = groups[currentGroup][0];
            }
            var userIds = "";
            var endsWithComma = false;
            var userNos = currentGroupUsers.split(",");
            for (var i = 1; i < userNos.length - 1; i++) {
                userIds += users[userNos[i]].userId + ",";
                endsWithComma = true;
            }
        }
        if (endsWithComma) {
            userIds = userIds.substring(0, userIds.length - 1);
        }
        accessManager.addGroup(currentGroupName, userIds, SECURITY_TOKEN).addCallback(function(values, exception) {
            if (exception) {
                //            alert(exception.msg);
                //            return;
                adminLogout();
            }
            populateGroups();
            alert(currentGroupName + " Group Saved");
        });
    }
}

function deleteGroup() {
    var groupName = groups[currentGroup][0];
    accessManager.deleteGroup(groups[currentGroup][0], SECURITY_TOKEN).addCallback(function(values, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        currentGroup = -1;
        alert(groupName + " Group Deleted");
        populateGroups();
    });
}

//******************************************************************************************************************************
//super admin creating roles


function populateSuperAdminRoles() {
    accessManager = new dojo.rpc.JsonService(photark.constants.AccessManagerServiceEndpoint);
    accessManager.getRoles().addCallback(function(roles, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        this.roles = roles;
        var addRole = dojo.byId("superAdminSelectRole");
        addRole.options.length = 0;
        //        if (permissions.indexOf("|createGroupRole|") != -1) {
        addRole.options[addRole.options.length] = new Option("-New Role-", -1, true, false);
        //        }
        for (var pos = 0; pos < roles.length; pos++) {
            addRole.options[addRole.options.length] = new Option(roles[pos].roleName, pos, false, false);
        }
        dojo.byId("superAdminSelectRole").value = currentSuperAdminRole;
        if (allPermissions == undefined) {
            getAllPermissions();
        } else {
            manageSuperAdminRoleFields();
        }
    });
}

function getAllPermissions() {
    accessManager.getPermissions().addCallback(function(permissions, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        this.allPermissions = permissions;
        manageSuperAdminRoleFields();
    });
}

function manageSuperAdminRoleFields() {
    currentSuperAdminRole = dojo.byId("superAdminSelectRole").value;
    var table = "<table border=\"1\" style=\"width:60%;\">\n" +
            "<tr>\n" +
            "<td><b>Permitted</b></td>\n" +
            "<td><b>Permission</b></td>\n" +
            "<td><b>Permission Description</b></td>\n" +
            "</tr>";
    if (currentSuperAdminRole != -1 && roles[currentSuperAdminRole].roleName == "allUsersViewRole") {
        dojo.byId("deleteSuperAdminRole").style.display = "none";
        dojo.byId("saveSuperAdminRole").style.display = "none";
        dojo.byId("newSuperAdminRoleLabel").style.display = "none";
        dojo.byId("newSuperAdminRoleName").style.display = "none";
        table += "<tr>\n" +
                "<td><input type=\"checkbox\" CHECKED DISABLED/></td>\n" +
                "<td><label >viewImages</label></td>\n" +
                "<td><label >Allow all the users to view the album images</label></td>\n" +
                "</tr>";
    } else {
        for (var i = 0; i < allPermissions.length; i++) {
            table += "<tr>\n";
            if (currentSuperAdminRole != -1) {
                dojo.byId("deleteSuperAdminRole").style.display = "";
                dojo.byId("saveSuperAdminRole").style.display = "";
                dojo.byId("newSuperAdminRoleLabel").style.display = "none";
                dojo.byId("newSuperAdminRoleName").style.display = "none";
                var permissionAdded = false;
                for (var j = 0; roles[currentSuperAdminRole].permissions.length > j; j++) {
                    if (roles[currentSuperAdminRole].permissions[j].permission == allPermissions[i].permission) {
                        table += "<td><input type=\"checkbox\" id=\"" + i + "_permission\" CHECKED /></td>\n";
                        permissionAdded = true;
                        break;
                    }
                }
                if (!permissionAdded) {
                    table += "<td><input type=\"checkbox\" id=\"" + i + "_permission\"/></td>\n";
                }
            } else {
                dojo.byId("deleteSuperAdminRole").style.display = "none";
                dojo.byId("saveSuperAdminRole").style.display = "";
                dojo.byId("newSuperAdminRoleLabel").style.display = "";
                dojo.byId("newSuperAdminRoleName").style.display = "";
                table += "<td><input type=\"checkbox\" id=\"" + i + "_permission\"/></td>\n";
            }
            table += "<td><label >" + allPermissions[i].permission + "</label></td>\n" +
                    "<td><label >" + allPermissions[i].permissionDesc + "</label></td>\n" +
                    "</tr>";
        }
    }
    table += "  </table>";
    document.getElementById('superAdminRolePermissionTableDiv').innerHTML = table;
}

function saveSuperAdminRole() {
    if (confirm("Are you sure you want to save the changes ?")) {
        var currentRoleGroupName;
        if (currentSuperAdminRole == -1) {
            var name = dojo.byId("newSuperAdminRoleName").value;
            if (name == null || (name != null && name == "" && name.length == 0)) {
                alert("Role cannot be created. Enter a role name!");
                return;
            }
            currentSuperAdminRole = dojo.byId("superAdminSelectRole").options.length - 1;
            currentRoleGroupName = name;
        } else {
            if (roles[currentSuperAdminRole].roleName == "allUsersViewRole") {
                alert("This role cannot be changed!");
                return;
            }
            currentRoleGroupName = roles[currentSuperAdminRole].roleName;
        }
        var permissions = "";
        var endsWithComma = false;
        for (var i = 0; allPermissions.length > i; i++) {
            if (document.getElementById(i + '_permission').checked) {

                permissions += allPermissions[i].permission + ",";
                endsWithComma = true;
            }
        }
        if (endsWithComma) {
            permissions = permissions.substring(0, permissions.length - 1);
        }
        accessManager.addRole(currentRoleGroupName, permissions, SECURITY_TOKEN).addCallback(function(values, exception) {
            if (exception) {
                //            alert(exception.msg);
                //            return;
                adminLogout();
            }
            populateSuperAdminRoles();
            dojo.byId("newSuperAdminRoleName").value = "";
            alert(currentRoleGroupName + " Role Saved");
        });
    }
}
function confirmRoleDelete() {
    if (confirm("Are you sure to delete the role " + roles[currentSuperAdminRole].roleName + "?")) {
        deleteRole(roles[currentSuperAdminRole].roleName);
    }
}

function deleteRole(roleName) {
    accessManager.deleteRole(roleName, SECURITY_TOKEN).addCallback(function(values, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        currentSuperAdminRole = -1;
        alert(roleName + " Role Deleted");
        populateSuperAdminRoles();
    });
}


//******************************************************************************************************************************
//super admin block user


function populateSuperAdminBlockUser() {
    accessManager = new dojo.rpc.JsonService(photark.constants.AccessManagerServiceEndpoint);
    accessManager.getAllUsers().addCallback(function(users, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        this.users = users;
        var addUser = dojo.byId("superAdminSelectBlockUser");
        addUser.options.length = 0;
        //        if (permissions.indexOf("|createGroupRole|") != -1) {
        addUser.options[addUser.options.length] = new Option("-Select User:UserId-", "-Select User-", true, false);
        //        }
        for (var pos = 2; pos < users.length; pos++) {
            if (users[pos].userId != userId) {
                addUser.options[addUser.options.length] = new Option(users[pos].userInfo.displayName + ":" + users[pos].userId, pos, false, false);
            }
        }
        dojo.byId("superAdminSelectBlockUser").value = "-Select User-";
        dojo.byId("addSuperAdminBlockUser").style.display = "none";
        getSuperAdminBlockUserList();
        manageSuperAdminBlockUserFields();
    });
}

function getSuperAdminBlockUserList() {
    accessManager = new dojo.rpc.JsonService(photark.constants.AccessManagerServiceEndpoint);
    accessManager.getUsersFromList("blockedUserList", SECURITY_TOKEN).addCallback(function(blockedUsers, exception) {
        if (exception) {
            //            alert(exception.msg);
            //            return;
            adminLogout();
        }
        this.blockedUsers = blockedUsers;
        showBlockedUsers();
    });
}

function manageSuperAdminBlockUserFields() {
    if (dojo.byId("superAdminSelectBlockUser").value == "-Select User-") {
        dojo.byId("addSuperAdminBlockUser").style.display = "none";
    } else {
        dojo.byId("addSuperAdminBlockUser").style.display = "";
    }
}

function showBlockedUsers() {
    var table = "<table border=\"1\" style=\"width:60%;\">\n" +
            "<tr>\n" +
            "<td><b>User Name</b></td>\n" +
            "<td><b>User's OpenId</b></td>\n" +
            "<td><b>Unblock User</b></td>\n" +
            "</tr>";
    for (var i = 0; blockedUsers.length > i; i++) {
        table += "<tr>\n" +
                "<td><label>" + blockedUsers[i].userInfo.displayName + "</label></td>\n" +
                "<td><label>" + blockedUsers[i].userId + "</label></td>\n" +
                "<td><a href=\"javascript:confirmUnblockUser('" + blockedUsers[i].userInfo.displayName + "','" + blockedUsers[i].userId + "'," + i + ");\">Unbolck</a></td>\n" +
                "</tr>";
    }
    table += "  </table>";
    document.getElementById('superAdminBlockUserTableDiv').innerHTML = table;
}

function confirmUnblockUser(userName, userId, blockedUsersNo) {
    // if (confirm("Do you want to unblock " + userName + "?")) {     //not implemented for now
    var newBlockedUsers = new Array();
    var j = 0;
    for (var i = 0; blockedUsers.length > i; i++) {
        if (blockedUsers[i].userId != userId) {
            newBlockedUsers[j] = blockedUsers[i];
            j++
        }
    }
    blockedUsers = newBlockedUsers;
    showBlockedUsers();
    // }
}

function addSuperAdminBlockUser() {
    if (dojo.byId("superAdminSelectBlockUser").value != "-Select User-") {
        var userId = users[dojo.byId("superAdminSelectBlockUser").value].userId;
        var userAdded = false;
        for (var i = 0; blockedUsers.length > i; i++) {
            if (blockedUsers[i].userId == userId) {
                userAdded = true;
                alert("User already added to the Block List");
                return;
            }
        }
        if (!userAdded) {
            blockedUsers[blockedUsers.length] = users[dojo.byId("superAdminSelectBlockUser").value];
            showBlockedUsers();
        }
    }
}

function saveSuperAdminBlockUsers() {
    if (confirm("Are you sure you want to save the changes ?")) {
        var userIds = new Array();
        for (var i = 0; blockedUsers.length > i; i++) {
            userIds[i] = blockedUsers[i].userId;
        }
        accessManager.replaceUsersInList(userIds, "blockedUserList", SECURITY_TOKEN).addCallback(function(values, exception) {
            if (exception) {
                //            alert(exception.msg);
                //            return;
                adminLogout();
            }
            getSuperAdminBlockUserList();

            alert("Blocked User List Saved");
        });
    }
}
