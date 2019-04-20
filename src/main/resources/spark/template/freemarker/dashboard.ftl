<#assign content>
<div id="dashboard-body">

<div id="side-menu">
    <div id="side-menu-content">
        <div id="department-dropdown">
        <select id="department-selector">
            <!-- HARD CODED OPTIONS FOR THE MOMENT; WILL BE OPTIONS FROM BACKEND IN FUTURE-->
            <option value="Applied Math">Applied Math</option>
            <option value="Archeology">Archeology</option>
            <option value="Biology">Biology</option>
            <option value="Computer Science">Computer Science</option>
        </select>
        </div>

        <div id="class-selector">
            <!-- Populate list with classes from the selected department -->
            <ul id="class-list">
            </ul>
        </div>

        <div id="update-button-wrapper">
            <button id="update">Update</button>
        </div>
    </div>
</div>

<div id="group-display">

    <div id="sort-bar">
        <p> Sort by: </p>
        <div id="sort-dropdowns">
        <select id="order">
            <option value="asc">Ascending</option>
            <option value="desc">Descending</option>
        </select>
        <select id="sort">
            <option value="course code">Course Code</option>
            <option value="group size">Group Size</option>
            <option value="distance">Distance to Me</option>
        </select>
        </div>
    </div>

    <div id="group-grid">
        <div id="add-group-button">
            <div id="add-group-text">
                <a href=/study/newgroup id="plus-sign">+</a>
                <a href=/study/newgroup id="add-group">Add Group</a>
            </div>
        </div>
    </div>

</div>

</div>
</#assign>
<#include "main.ftl">
