
$(document).ready(() => {

    const $select_course = $('#select-course-number');
    const $select_department = $('#select-department');
    const $create_group = $('#create-group');

    repopulateCourses();

    // When department changes, repopulate course list
    $select_department.on('change', event => {
        repopulateCourses();
    });

    // When create group button is clicked, first check all fields complete then
    // send post request with information to back end
    $create_group.on('click', event => {

        const dept = $select_department.val();
        const grouptitle = $('#field-title').val();
        const course = $select_course.val();
        const hours = $('#field-duration-hours').val();
        const mins = $('#field-duration-mins').val();
        const description = $('#field-description').val();
        const building = $('#select-building').val();
        const loc = $('#field-location').val();

        // Define the fields that need to be completed to continue
        const fields = [
                        $('#field-title'),
                        $('#field-duration-hours'),
                        $('#field-duration-mins'),
                        $('#field-description'),
                        $('#field-location')
                        ];
        // Keep counter for number of fields that are incomplete
        let incomplete = 0;

        // Change the border color to red for any fields that is incomplete, or
        // return to gray is completed
        for (i in fields) {
            if (fields[i].val() === "") {
                fields[i].css('border', '2px red solid');
                incomplete = incomplete + 1;
            } else {
                fields[i].css('border', '1px gray solid');
            }
        }
        // If any field is incomplete, display the error
        if (incomplete != 0) {
            $('#incomplete-form-error').empty();
            $('#incomplete-form-error').append("You must complete all fields before creating a new group!");
        }
        // If all fields are complete, remove the error and proceed to the POST request
        else {
            $('#incomplete-form-error').empty();


            // Send all new group data to back end
            const postParameter = { department: dept,
                                    grouptitle: grouptitle,
                                    course_number: course,
                                    duration_hours: hours,
                                    duration_mins: mins,
                                    description: description,
                                    building: building,
                                    location: loc,
                                    email: localStorage.getItem("grouper_email")
                                  };

            // Creates new groups with data and returns the group id for the newly
            // created group
            $.post("/createGroupInfo", postParameter, responseJSON => {

                const responseObject = JSON.parse(responseJSON);
                const id = responseObject.groupid;
                window.location.href = "/grouper/group?gid="+id+"&uid=modPage";
                localStorage.setItem("isModerator", true);
            });

            // Call fuction in websockets.js
            update_dash();
        }
    });

    // Repopulates course dropdown based on department selected
    function repopulateCourses() {
        const department = $select_department.val();
        const postParameter = {department: department};

        $.post("/populateCourses", postParameter, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            const courses = responseObject.dept;

            // Empty old results
            $select_course.empty();

            // Display new results
            for (i in courses) {
                $select_course.append(
                    "<option value='" + courses[i] + "'>" + courses[i] + "</option>"
                );
            }
        });
    }

});
