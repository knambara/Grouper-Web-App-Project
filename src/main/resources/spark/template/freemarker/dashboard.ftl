<#assign content>
<div id="dashboard-body">

<div id="side-menu">
    <div id="user-info">
    <div id="email-and-img">
    <div class="circle-image" id="user-img"></div>
    <p>${email}</p>
    </div>
    <button id="log-out">log out</button>
    </div>
    <div id="side-menu-content">
        <div id="dashboard-logo">
        <h1>Grouper</h1>
        <img width="120" src="/img/grouper-icon.svg"/>
        </div>
        <div id="department-dropdown">
        <select id="department-selector">
            <option value="none">---select a department---</option>
            <#list departments as depts>
            <option value="${depts}">${depts}</option>
            </#list>
        </select>
        </div>

        <div id="class-selector">
            <!-- Populate list with classes from the selected department -->
            <ul id="class-list">
            </ul>
        </div>

        <!--
        <div id="update-button-wrapper">
            <button id="update">Update</button>
        </div>
        -->
    </div>
</div>

<div id="group-display">

    <div id="own-group">
      <a href=${grouplink}>${groupname}</a>
    </div>

    <div id="sort-bar">
        <p> Sort by: </p>
        <div id="sort-dropdowns">
        <select id="order">
            <option value="asc">Ascending</option>
            <option value="desc">Descending</option>
        </select>
        <select id="sort">
            <option value="none">---</option>
            <option value="course-code">Course Code</option>
            <option value="group-size">Group Size</option>
            <option value="distance">Distance to Me</option>
            <option value="time-rem"> Time Remaining </option>
        </select>
        </div>
    </div>

    <div id="group-grid">
        <div id="add-group-button" class="group-button">
            <div id="add-group-text">
                <p id='plus-sign'>+</p><p id='add-group'>Add Group</p></div>
                <!--<a href=/grouper/newgroup id="plus-sign">+</a>
                <a href=/grouper/newgroup id="add-group">Add Group</a> -->
            </div>
        </div>
    </div>

</div>

</div>
</#assign>
<#include "main.ftl">

<script>
$(document).ready(function() {
  console.log("dashboard");
  setup_live_groups();
});
</script>
