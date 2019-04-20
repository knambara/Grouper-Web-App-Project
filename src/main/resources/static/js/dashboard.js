class GroupTile {
    constructor(id, title, course, size, loc, time_left) {
        this.id = id;
        this.title = title;
        this.course = course;
        this.size = size;
        this.loc = loc;
        this.time_left = time_left;
    }

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
        "<p>" + this.time_left + " left</p>" +
        "</div></div>"
        ;
        $('#group-grid').prepend(tile);
    }

    hide() {
        $('#'+this.id).remove();
    }
}

let allTiles = new Array(0);

// Groups for Test Purposes
const cs22_groups = [[1, "Studying for Midterm", "CSCI 0220", 5, "CIT", "2:22"],[2, "Drawing Logic Circuits", "CSCI 0220", 4, "Science Library", "0:43"]];
const cs32_groups = [[3, "Talking about Appliances", "CSCI 0320", 37, "CIT", "4:12"]];

const $dept_select = $('#department-selector');
const $class_list = $('#class-list');
const $update_button = $('#update');

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

    // Update courses w/ active groups when the department selector is changed
    $dept_select.on('change', event => {

        // Remove old results
        $class_list.empty();
        for (t in allTiles) {
            allTiles[t].hide();
        }
        allTiles = new Array(0);

        // Get current info
        const curr_department = $dept_select.val();
        const curr_classes = class_map.get(curr_department);

        // TODO: Send POST request to get all classes with active groups?

        // Add each class to the list
        for (let i in curr_classes) {
            $class_list.append("<li> <input type='checkbox' name='classes' class='class' value='"+ curr_classes[i] + "'>" + curr_classes[i] + "</li>");
        }

    });

    // Display active groups for selected classes when Update button is clicked
    $update_button.on('click', event => {

        const checked_classes = new Array(0);
        const curr_classes = $('.class');

        for (t in allTiles) {
            allTiles[t].hide();
        }
        allTiles = new Array(0);

        for (let i in curr_classes) {
            if (curr_classes[i].checked) {
                //console.log(curr_classes[i].value + " is checked");
                checked_classes.push(curr_classes[i])
            }
        }

        // TODO: Display active groups for checked classes

        // IDEA: POST request for all groups associated with the checked_classes =>
        // list of group information in format [id, title, course, size, location, time-remaining]
        // for each group

        // get groups associated w/ checked classes
        for (i in checked_classes) {
            if (checked_classes[i].value === "CSCI 0220") {
                for (g in cs22_groups) {
                    const group = cs22_groups[g];
                    const tile = new GroupTile(group[0], group[1], group[2], group[3], group[4], group[5]);
                    allTiles.push(tile);
                    tile.build();
                }
            }
            if (checked_classes[i].value === "CSCI 0320") {
                for (g in cs32_groups) {
                    const group = cs32_groups[g];
                    const tile = new GroupTile(group[0], group[1], group[2], group[3], group[4], group[5]);
                    allTiles.push(tile);
                    tile.build();
                }
            }
        }
    });

});
