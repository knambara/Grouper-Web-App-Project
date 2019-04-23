class GroupTile {
    constructor(id, title, course, size, loc, time_left) {
        this.id = id;
        this.title = title;
        this.dept = course.substring(0,4);
        this.code = course.substring(4,8);
        this.size = size;
        this.loc = loc;
        this.time_left = time_left; // in minutes
    }

    // Builds the HTML of the Group Tile
    build() {
        const tile = document.createElement('div');
        tile.setAttribute('id', this.id);
        tile.setAttribute('class', 'group-tile');
        tile.innerHTML ="<div class='group-tile-content'><h2>"+this.title + "</h2>" +
        "<h4>" + this.dept + " " + this.code + "</h4>" +
        "<p>" + this.loc + "</p>" +
        "<div id='group-tile-bottom'>" +
        "<button id='join'>Join</button>" +
        "<p>" + this.size + " member(s)</p>" +
        "<div id='time-"+ this.id + "'>" + this.time_left + " left</div>" +
        "</div></div>"
        ;
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

    // Converts time left to hours and minutes adn updates the HTML
    updateTime() {
        const hours = Math.floor(this.time_left / 60);
        const mins = this.time_left - hours*60;
        if (mins >= 10) {
            $('#time-'+ this.id).html(hours + ":" + mins + " left");
        } else {
            $('#time-'+ this.id).html(hours + ":0" + mins + " left");
        }
    }
}

// Rebuilds the group-grid in the correctly sorted order
function rebuildGrid(sortedTiles) {
    $group_grid.empty();
    for (i in sortedTiles) {
        sortedTiles[i].build();
        sortedTiles[i].updateTime();
    }
    const addGroup = document.createElement('div');
    addGroup.setAttribute('id', 'add-group-button');
    addGroup.innerHTML = "<div id='add-group-text'>" +
        "<a href=/grouper/newgroup id='plus-sign'>+</a><a href=/grouper/newgroup id='add-group'>Add Group</a></div>";
    $group_grid.append(addGroup);
}

// Groups currently displayed stored in map where key = id, value = GroupTile
let displayedGroups = new Map();

const $dept_select = $('#department-selector');
const $class_list = $('#class-list');
const $update_button = $('#update');
const $order_select = $('#order');
const $sort_select = $('#sort');
const $group_grid = $('#group-grid');

const class_map = new Map();

$(document).ready(() => {

    // Begin updating time remaining
    updateTimeRemainingForDisplayed();

    // Update courses w/ active groups when the department selector is changed
    $dept_select.on('change', event => {

        // Remove old results
        $class_list.empty();

        // Get current info
        const curr_department = $dept_select.val();

        // Send POST request to get all classes with active groups
        const postParameters = {department: curr_department};

        $.post("/department", postParameters, responseJSON => {

            const responseObject = JSON.parse(responseJSON);
            const curr_classes = responseObject.classes;

            // Add each class to the list
            for (let i in curr_classes) {
                $class_list.append("<li>" + curr_classes[i] +
                "<input type='checkbox' name='classes' class='class-item' value='"+ curr_classes[i] + "'>" +
                 "</li>");
            }

        });
    });

    // Display active groups for selected classes when Update button is clicked
    $update_button.on('click', event => {

        const shown_classes = $('.class-item');
        const checked_classes = [];

        for (let i = 0; i < shown_classes.length; i++) {
            if (shown_classes[i].checked) {
                checked_classes.push(shown_classes[i].value);
            }
        }

        // Probably not the most effective way of removing groups associated
        // with unchecked classes, but it seems to work.
        $group_grid.empty();
        displayedGroups = new Map();
        const addGroup = document.createElement('div');
        addGroup.setAttribute('id', 'add-group-button');
        addGroup.innerHTML = "<div id='add-group-text'>" +
            "<a href=/grouper/newgroup id='plus-sign'>+</a><a href=/grouper/newgroup id='add-group'>Add Group</a></div>";
        $group_grid.append(addGroup);

        // Send list of course codes as strings
        const postParameters = {checked: JSON.stringify(checked_classes)};

        // POST request for all groups associated with checked classes; recieve list of
        // active groups w/ information in formate of [id, title, course, size, location, time-remaining]
        $.post("/checkedClasses", postParameters, responseJSON => {

            const responseObject = JSON.parse(responseJSON);
            const groups = responseObject.groups;

            for (i in groups) {
                const group = groups[i];
                if (!displayedGroups.has(group[0])) {
                    const tile = new GroupTile(group[0], group[1], group[2], group[3], group[4], group[5]);
                    displayedGroups.set(tile.id, tile);
                    tile.build();
                    tile.updateTime();
                }
            }
            sort();
        });
    });

    function updateTimeRemainingForDisplayed() {
        const groups = displayedGroups.values();
        for (let i = 0; i < displayedGroups.size; i++) {
            const g = groups.next().value;
            g.setTime(g.time_left-1);
            g.updateTime();
        }
        t = setTimeout(updateTimeRemainingForDisplayed,60000);
    }


    // SORTING FUNCTIONALITY

    $order_select.on('change', event => {
        sort();
    });

    $sort_select.on('change', event => {
        sort();
    });

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
});
