<#assign content>
<div id="dashboard-body">

<div id="side-menu">

    <div id="department-dropdown">
    <select id="department-selector">
        <!-- HARD CODED OPTIONS FOR THE MOMENT; WILL BE OPTIONS FROM BACKEND IN FUTURE-->
        <option value="Applied Math">Applied Math</option>
        <option value="Archeology">Archeology</option>
        <option value="Biology">Biology</option>
        <option value="Computer Science">Computer Science</option>
        <option value="Comparative Literature">Comparative Literature</option>
        <option value="Economics">Economics</option>
        <option value="Physics">Physics</option>
    </select>
    </div>

    <div id="class-selector">
        <!-- Populate list with classes from the selected department -->
        <ul id="class-list">
        </ul>
    </div>

</div>

</div>
</#assign>
<#include "main.ftl">
