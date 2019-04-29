$(document).ready(() => {

    const $select_course = $('#select-course-number');
    const $select_department = $('#select-department');
    const $create_group = $('#create-group');

    repopulateCourses();

    // When department changes, repopulate course list
    $select_department.on('change', event => {
        repopulateCourses();
    });


    $create_group.on('click', event => {

        const dept = $select_department.val();
        const grouptitle = $('#field-title').val();
        const course = $select_course.val();
        const hours = $('#field-duration-hours').val();
        const mins = $('#field-duration-mins').val();
        const description = $('#field-description').val();
        const building = $('#select-building').val();
        const loc = $('#field-location').val();

        console.log(dept, course, hours, mins, description, building, loc);
        //if (grouptitle === "" || description === "" || loc === "" || hours === "" || mins === "") {
        //    displayFormError();
        const fields = [$('#field-title'), $('#field-duration-hours'), $('#field-duration-mins'), $('#field-description'), $('#field-location')];
        let incomplete = 0;
        for (i in fields) {
            if (fields[i].val() === "") {
                fields[i].css('border', '2px red solid');
                incomplete = incomplete + 1;
            } else {
                fields[i].css('border', '1px gray solid');
            }
        }
        if (incomplete != 0) {
            $('#incomplete-form-error').empty();
            $('#incomplete-form-error').append("You must complete all fields before creating a new group!");
        } else {
            $('#incomplete-form-error').empty();

            // Also need to send User data? In order to designate moderator, add email/member?

            // Send all new group data to back end
            const postParameter = {department: dept, grouptitle: grouptitle,
                course_number: course, duration_hours: hours, duration_mins: mins,
                description: description, building: building, location: loc, email: localStorage.getItem("grouper_email")};

            // Creates new groups with data and returns the URL corresponding to
            // that group, which the user is sent to
            $.post("/createGroupInfo", postParameter, responseJSON => {

                const responseObject = JSON.parse(responseJSON);
                const url = responseObject.groupurl;
                const id = responseObject.groupid;
                console.log(url);
                window.location.href = url + "?gid="+id+"&uid=modPage";
            });

            // Call fuction in websockets.js
            update_dash();
            }
    });

    function repopulateCourses() {
        const department = $select_department.val();
        const postParameter = {department: department};

        $.post("/populateCourses", postParameter, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            const courses = responseObject.dept;

            // Empty old results
            $select_course.empty();

            for (i in courses) {
                $select_course.append(
                    "<option value='" + courses[i] + "'>" + courses[i] + "</option>"
                );
            }
        });
    }

});

function displayFormError() {
    $('#incomplete-form-error').empty();
    $('#incomplete-form-error').append("You must complete all fields before creating a new group!");
}



// let populateDropdowns = function() {
//   getDepartments().forEach(dep => {
//     $("#select-department").append(
//       "<option value='" + dep + "'>" + dep + "</option>"
//     );
//   });
//
//   getCourses().forEach(course => {
//     $("#select-course-number").append(
//       "<option value='" + course + "'>" + course + "</option>"
//     );
//   });
//
//   getBuildings().forEach(building => {
//     $("#select-building").append(
//       "<option value='" + building + "'>" + building + "</option>"
//     );
//   });
// }
//
// let getDepartments = function() {
//   return ["Applied Mathematics", "Computer Science", "Philosophy"];
// }
//
// let getCourses = function(department) {
//   return ["APMA1650", "CSCI0320", "CSCI0220", "PHIL0010"];
// }
//
// let getBuildings = function() {
//   return ["Barus and Holley", "Faunce House", "Sayles Hall", "Sciences Library"];
// }
