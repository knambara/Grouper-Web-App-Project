$(document).ready(() => {
  populateDropdowns();
});

let populateDropdowns = function() {
  getDepartments().forEach(dep => {
    $("#select-department").append(
      "<option value='" + dep + "'>" + dep + "</option>"
    );
  });

  getCourses().forEach(course => {
    $("#select-course-number").append(
      "<option value='" + course + "'>" + course + "</option>"
    );
  });

  getBuildings().forEach(building => {
    $("#select-building").append(
      "<option value='" + building + "'>" + building + "</option>"
    );
  });
}

let getDepartments = function() {
  return ["Applied Mathematics", "Computer Science", "Philosophy"];
}

let getCourses = function(department) {
  return ["APMA1650", "CSCI0320", "CSCI0220", "PHIL0010"];
}

let getBuildings = function() {
  return ["Barus and Holley", "Faunce House", "Sayles Hall", "Sciences Library"];
}
