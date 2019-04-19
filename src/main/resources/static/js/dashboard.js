$(document).ready(() => {

    const $dept_select = $('#department-selector');

    console.log('ready');

    $dept_select.onchange = function() {
        console.log("changed");
    };

});
