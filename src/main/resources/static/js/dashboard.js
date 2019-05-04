
// Class defining a Group Tile for the Dashboard page
class GroupTile {
    constructor(id, title, course, size, loc, time_left, end_time) {
        this.id = id;
        this.title = title;
        this.dept = course.substring(0,4);
        this.code = course.substring(4,8);
        this.size = size;
        this.loc = loc;
        this.time_left = time_left;
        this.endTime = end_time; // Timestamp
    }

    // Builds the HTML of the Group Tile
    build() {
        const tile = document.createElement('div');
        tile.setAttribute('id', this.id);
        tile.setAttribute('class', 'group-tile');
        const html1 ="<div class='group-tile-content'><h2>"+this.title + "</h2>" +
        "<h4>" + this.dept + " " + this.code + "</h4>" +
        "<p>" + this.loc + "</p>" +
        "<div id='group-tile-bottom'>";
        let html2 = "";
        // If user is not in the group, button says "Join", otherwise it says "View"
        if (this.id != localStorage.getItem("gid")) {
            html2 = "<button onclick='manageJoin("+this.id+")' class='join' id='join"+this.id + "'>Join</button>";
        } else {
            html2 = "<button onclick='manageJoin("+this.id+")' class='join' id='view"+this.id + "'>View</button>";
        }
        const html3 = "<p>" + this.size + " member(s)</p>" +
        "<div id='time-"+ this.id + "'>" + this.time_left + " left</div>" +
        "</div></div>";
        ;
        tile.innerHTML = html1 + html2 + html3;
        $group_grid.prepend(tile);
    }

    // Removes HTML from DOM
    hide() {
        $('#'+this.id).remove();
    }

    // Mutator for time left attribute
    setTime(time) {
        this.time_left = time;
    }

    // Finds time remaining based on current time and end time
    updateTime() {
        const endTime = new Date(this.endTime);
        const currTime = new Date();
        const timeDiff = endTime.getTime() - currTime.getTime();
        const totalMins = timeDiff/ 60000;
        const hours = Math.floor(totalMins / 60);
        const mins = Math.floor(totalMins - hours*60);
        if (mins >= 10) {
            $('#time-'+ this.id).html(hours + ":" + mins + " left");
        } else {
            $('#time-'+ this.id).html(hours + ":0" + mins + " left");
        }
    }
}

// Function to determine if a user can join a new group or not
function manageJoin(id) {
    const curr_gid = localStorage.getItem('gid');
    const isMod = localStorage.getItem('isModerator');

    // If you are already in  a group
    if (curr_gid != "-1") {
        // Clicking the group you are already in brings you back to that group page
        if (id == curr_gid) {
            /*if (isMod) {
                window.location.href= "/grouper/group?gid="+id+"&uid=modPage";
            }
            else {*/
                window.location.href= "/grouper/group?gid=" + id + "&uid="+ localStorage.getItem("grouper_hash");
            //}

        }
        // Trying to join a group you are NOT already in sends an alert
        else {
            alert("Whoa there... Looks like you're already in a group! You must leave your current group before joining a new one!");
        }
    }
    // If you're not in any group you can join any group
    else {
        window.location.href= "/grouper/group?gid=" + id + "&uid="+ localStorage.getItem("grouper_hash");
    }
}

// Rebuilds the group-grid in the correctly sorted order based on input parameter
function rebuildGrid(sortedTiles) {
    $group_grid.empty();
    for (i in sortedTiles) {
        sortedTiles[i].build();
        sortedTiles[i].updateTime();
    }
    const addGroup = document.createElement('div');
    addGroup.setAttribute('id', 'add-group-button');
    addGroup.innerHTML = "<div id='add-group-text'>" +
        "<p id='plus-sign'>+</p><p id='add-group'>Add Group</span></p>";
    $group_grid.append(addGroup);
}

function updateGrid() {
        const shown_classes = $('.class-item');
        const checked_classes = [];

        for (let i = 0; i < shown_classes.length; i++) {
            if (shown_classes[i].checked) {
                checked_classes.push(shown_classes[i].value);
            }
        }
        console.log(checked_classes);
        // Send list of course codes as strings
        const postParameters = {checked: JSON.stringify(checked_classes)};

        // POST request for all groups associated with checked classes; recieve list of
        // active groups w/ information in formate of [id, title, course, size, location, time-remaining, end-time]
        $.post("/checkedClasses", postParameters, responseJSON => {

            const responseObject = JSON.parse(responseJSON);
            const groups = responseObject.groups;

            // Build a GroupTile object for every group returned (if not already displayed)
            for (i in groups) {
                const group = groups[i];
                if (!displayedGroups.has(group[0])) {
                    const tile = new GroupTile(group[0], group[1], group[2], group[3], group[4], group[5], group[6]);
                    displayedGroups.set(tile.id, tile);
                    tile.build();
                    tile.updateTime();
                }
            }

            sort();
            sessionStorage.setItem("checkedClasses", JSON.stringify(checked_classes));
        });
    }


// Function to sort the grid based on whatever the value of each dropdown is
function sort() {
    // Sort based on course code
    if ($sort_select.val() === "course-code") {
        const sorted = sortByCourseCode(displayedGroups);
        if ($order_select.val() === "asc") {
            rebuildGrid(sorted);
        } else {
            rebuildGrid(sorted.reverse());
        }
    }
    // Sort based on time remaining
    else if ($sort_select.val() === "time-rem") {
        const sorted = sortByTimeLeft(displayedGroups);
        if ($order_select.val() === "asc") {
            rebuildGrid(sorted);
        } else {
            rebuildGrid(sorted.reverse());
        }
    }
    // Sort based on group size
    else if ($sort_select.val() === "group-size") {
        const sorted = sortByGroupSize(displayedGroups);
        if ($order_select.val() === "asc") {
            rebuildGrid(sorted);
        } else {
            rebuildGrid(sorted.reverse());
        }
    }
    // Sort based on distance to user
    /*
    else if ($sort_select.val() === "distance") {
        const sorted = sortByDistance(displayedGroups);
        if ($order_select.val() === "asc") {
            rebuildGrid(sorted);
        } else {
            rebuildGrid(sorted.reverse());
        }
    }
    */
}

// Groups currently displayed stored in map where key = id, value = GroupTile
let displayedGroups = new Map();

const $dept_select = $('#department-selector');
const $logout_button = $('#log-out');
const $class_list = $('#class-list');
const $update_button = $('#update');
const $order_select = $('#order');
const $sort_select = $('#sort');
const $group_grid = $('#group-grid');

const class_map = new Map();

$(document).ready(() => {

    // When page is refreshed, reload all the appropriate data that has been saved
    $(window).on('load', function(){

        const postParameters = {user: getUserSession().email, hash: getUserSession().hash};
        $.post("../getUserGroup", postParameters, responseJSON => {

            const responseObject = JSON.parse(responseJSON);

            if (responseObject.status != "failure") {
              setUserSessionGroup(responseObject.group);
            }

        });

        // Reset all dropdowns with previous values before refresh
        $dept_select.val(sessionStorage.getItem("department"));
        $order_select.val(sessionStorage.getItem("order"));
        $sort_select.val(sessionStorage.getItem("sort"));
        const curr_classes = JSON.parse(sessionStorage.getItem("classList"));
        const checked_classes = JSON.parse(sessionStorage.getItem("checkedClasses"));

        // Re-add all classes that were listed and checked
        for (let i in curr_classes) {
            $class_list.append("<li><label class='checkmark-container'>" + curr_classes[i] +
            "<input type='checkbox' name='classes' class='class-item' value='"+ curr_classes[i] + "' id='"+curr_classes[i]+"'><span class='checkmark'></span></label" +
             "</li>");
            if (checked_classes.includes(curr_classes[i])) {
                $('#'+curr_classes[i]).prop('checked', true);
            }
            $('#'+curr_classes[i]).on('click', event => {
                // If checkbox is changed to unchecked, remove all groups
                //associated with the class, then update the grid
                if (!$('#'+curr_classes[i]).prop('checked')) {
                    removeGroups(curr_classes[i]);
                }
                updateGrid();

            });
        }
        updateGrid();
    });

    // Begin updating time remaining
    updateTimeRemainingForDisplayed();

    let old_dept = '';
    const dept_to_checked = new Map();

    // Update courses w/ active groups when the department selector is changed
    $dept_select.on('change', event => {

        //#### THIS IS ALL RELATED TO ATTEMPTING TO MAINTAIN EVERYTHING WHEN REFRESHING ####//
        // Before updating the classes, get all currently checked classes
        const shown_classes = $('.class-item');
        const old_checked_classes = [];

        for (let i = 0; i < shown_classes.length; i++) {
            if (shown_classes[i].checked) {
                old_checked_classes.push(shown_classes[i].value);
            }
        }
        // Map old department to old checked classes
        if (old_dept !== '') {
            dept_to_checked.set(old_dept, old_checked_classes);
        }
        //####################################################################//

        // Remove old results
        $class_list.empty();

        // Get current info
        const curr_department = $dept_select.val();
        sessionStorage.setItem("department", curr_department);

        // Send POST request to get all classes with active groups
        const postParameters = {department: curr_department};

        $.post("/department", postParameters, responseJSON => {

            const responseObject = JSON.parse(responseJSON);
            const curr_classes = responseObject.classes;

            // Determine which classes need to be re-checked
            let to_check;
            if (dept_to_checked.has(curr_department)) {
                to_check = dept_to_checked.get(curr_department);
            }

            // Add each class to the list
            for (let i in curr_classes) {
                $class_list.append("<li><label class='checkmark-container'>" + curr_classes[i] +
                "<input type='checkbox' name='classes' class='class-item' value='"+ curr_classes[i] + "' id='"+curr_classes[i]+"'><span class='checkmark'></span></label" +
                 "</li>");

                 // Check if class should already be checked
                 if (to_check) {
                     if (to_check.includes(curr_classes[i])) {
                         $('#'+curr_classes[i]).prop('checked', true);
                     }
                 }

                 $('#'+curr_classes[i]).on('click', event => {
                     // If checkbox is changed to unchecked, remove all groups
                     //associated with the class; then, update the grid
                     if (!$('#'+curr_classes[i]).prop('checked')) {
                         removeGroups(curr_classes[i]);
                     }
                    updateGrid();
                 });
            }
            sessionStorage.setItem("classList", JSON.stringify(curr_classes));
        });
        old_dept = curr_department;
    });

    // Remove all groups associated with the given class code
    function removeGroups(classCode) {
        const groups = displayedGroups.values();
        // Create new map to add all groups that won't be removed
        const tempDisplayedGroups = new Map();
        for (let i = 0; i < displayedGroups.size; i++) {
            const g = groups.next().value;
            if (g.dept + g.code === classCode) {
                g.hide();
            } else {
                tempDisplayedGroups.set(g.id, g);
            }
        }
        // Update displayedGroups variable to have only groups that should still
        // be shown after the given class code has been removed
        displayedGroups = tempDisplayedGroups;
    }

    // Updates time remaining on each tile every second
    function updateTimeRemainingForDisplayed() {
        const groups = displayedGroups.values();
        for (let i = 0; i < displayedGroups.size; i++) {
            const g = groups.next().value;
            g.updateTime();
        }
        t = setTimeout(updateTimeRemainingForDisplayed,1000);
    }


    // SORTING FUNCTIONALITY
    $order_select.on('change', event => {
        sessionStorage.setItem("order", $order_select.val());
        sort();
    });

    $sort_select.on('change', event => {
        sessionStorage.setItem("sort", $sort_select.val());
        sort();
    });

    // Log out functionality
    $logout_button.on('click', (e) => {
      removeSession();
      window.location.href = "/grouper";
    });

    // Prevent users who are already in a group from creating a new one.
    $('#add-group-button').on('click', event => {
        if (getUserSession().gid !== '-1') {
            alert("You are already in a group! You must leave or end your current group before adding a new one.");
        } else {
            window.location.href = "/grouper/newgroup";
        }
    });

});

// Sorting helper functions
function sortByCourseCode(groups) {
    const tempList = [];
    const groupIter = groups.values();
    for (let i = 0; i < groups.size; i++) {
        const g = groupIter.next().value;
        const tempGroup = {code: g.code, tile: g};
        tempList.push(tempGroup);
    }
    tempList.sort(function(a,b){return b.code - a.code;});
    const sortedTiles = [];
    for (i in tempList) {
        sortedTiles.push(tempList[i].tile);
    }
    return sortedTiles;
}

function sortByTimeLeft(groups) {
    const tempList = [];
    const groupIter = groups.values();
    for (let i = 0; i < groups.size; i++) {
        const g = groupIter.next().value;
        const tempGroup = {time: g.time_left, tile: g};
        tempList.push(tempGroup);
    }
    tempList.sort(function(a,b){return b.time - a.time;});
    const sortedTiles = [];
    for (i in tempList) {
        sortedTiles.push(tempList[i].tile);
    }
    return sortedTiles;
}

function sortByGroupSize(groups) {
    const tempList = [];
    const groupIter = groups.values();
    for (let i = 0; i < groups.size; i++) {
        const g = groupIter.next().value;
        const tempGroup = {size: g.size, tile: g};
        tempList.push(tempGroup);
    }
    tempList.sort(function(a,b){return b.size - a.size;});
    const sortedTiles = [];
    for (i in tempList) {
        sortedTiles.push(tempList[i].tile);
    }
    return sortedTiles;
}

function sortByDistance(groups) {

}
