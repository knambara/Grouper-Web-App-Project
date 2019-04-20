class GroupTile {
    constructor(id, title, course, size, loc, time_left) {
        this.id = id;
        this.title = title;
        this.course = course;
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
        "<h4>" + this.course + "</h4>" +
        "<p>" + this.loc + "</p>" +
        "<div id='group-tile-bottom'>" +
        "<button id='join'>Join</button>" +
        "<p>" + this.size + " member(s)</p>" +
        "<div id='time-"+ this.id + "'>" + this.time_left + " left</div>" +
        "</div></div>"
        ;
        $('#group-grid').prepend(tile);
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

// Groups currently displayed stored in map where key = id, value = GroupTile
let displayedGroups = new Map();

// Groups for Test Purposes
const cs22_groups = [[1, "Studying for Midterm", "CSCI 0220", 5, "CIT", 142],[2, "Drawing Logic Circuits", "CSCI 0220", 4, "Science Library", 43]];
const cs32_groups = [[3, "Talking about Appliances", "CSCI 0320", 37, "CIT", 252]];

const $dept_select = $('#department-selector');
const $class_list = $('#class-list');
const $update_button = $('#update');
const $order_select = $('#order');
const $sort_select = $('#sort');

const class_map = new Map();

// Hard-coded lists for development purposes; will be aquired from back end
const departments = ["Computer Science", "Biology", "Applied Math", "Archeology"];
const cs_classes = ["CSCI 0150", "CSCI 0220", "CSCI 0320"];
const bio_classes = ["BIOL 0200", "BIOL 1000"];
const apma_classes = ["APMA 0330", "APMA 1650"];
const arch_classes = ["ARCH 0123", "ARCH 0430"];
const classes = [cs_classes, bio_classes, apma_classes, arch_classes];

// Build map with departments and courses w/ active groups
for (let i in departments) {
    class_map.set(departments[i], classes[i]);
}


$(document).ready(() => {

    // Being updating time remaining
    updateTimeRemainingForDisplayed();

    // Update courses w/ active groups when the department selector is changed
    $dept_select.on('change', event => {

        // Remove old results
        $class_list.empty();

        // Get current info
        const curr_department = $dept_select.val();
        const curr_classes = class_map.get(curr_department);

        // TODO: Send POST request to get all classes with active groups?

        // Add each class to the list
        for (let i in curr_classes) {
            $class_list.append("<li>" + curr_classes[i] +
            "<input type='checkbox' name='classes' class='class' value='"+ curr_classes[i] + "'>" +
             "</li>");
        }

    });

    // Display active groups for selected classes when Update button is clicked
    $update_button.on('click', event => {

        //const checked_classes = new Array(0);
        const curr_classes = $('.class');

        /*
        for (let i in curr_classes) {
            if (curr_classes[i].checked) {
                //console.log(curr_classes[i].value + " is checked");
                checked_classes.push(curr_classes[i]);
            }
        }*/

        // TODO: Display active groups for checked classes

        // IDEA: POST request for all groups associated with the checked_classes =>
        // list of group information in format [id, title, course, size, location, time-remaining]
        // for each group
        // get groups associated w/ checked classes


        for (i in curr_classes) {
            //if (checked_classes[i].value === "CSCI 0220") {
            if (curr_classes[i].value === "CSCI 0220") {
                for (g in cs22_groups) {
                    const group = cs22_groups[g];
                    if (curr_classes[i].checked) {
                        if (!displayedGroups.has(group[0])) {
                            const tile = new GroupTile(group[0], group[1], group[2], group[3], group[4], group[5]);
                            displayedGroups.set(tile.id, tile);
                            tile.build();
                            tile.updateTime();
                        }
                    } else {
                        if (displayedGroups.has(group[0])) {
                            displayedGroups.get(group[0]).hide();
                            displayedGroups.delete(group[0]);
                        }
                }
                }
            }
            //if (checked_classes[i].value === "CSCI 0320") {
            if (curr_classes[i].value === "CSCI 0320") {
                for (g in cs32_groups) {
                    const group = cs32_groups[g];
                    if (curr_classes[i].checked) {
                        if (!displayedGroups.has(group[0])) {
                            const tile = new GroupTile(group[0], group[1], group[2], group[3], group[4], group[5]);
                            displayedGroups.set(tile.id, tile);
                            tile.build();
                            tile.updateTime();
                        }
                    } else {
                        if (displayedGroups.has(group[0])) {
                            displayedGroups.get(group[0]).hide();
                            displayedGroups.delete(group[0]);
                        }
                    }
                }
            }
        }
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
        console.log($order_select.val());
        console.log($sort_select.val());
    });

    $sort_select.on('change', event => {
        console.log($order_select.val());
        console.log($sort_select.val());
    });

    function sortByCourseCode(groups) {

    }

    function sortByTimeLeft(groups) {

    }

    function sortByGroupSize(groups) {

    }

    function sortByDistance(groups) {

    }

    function reverseOrder(groups) {

    }

});
