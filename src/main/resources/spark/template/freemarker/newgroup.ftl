<#assign content>
<div id="newgroup-body">

  <div id="newgroup-content">
    <h1>New Group</h1>
    <!-- form id="new-group-form" method="POST" -->

    <div id=""new-group-form">

      <div class="half-column-form">
        Department:<br>
        <select name="department" id="select-department">
          <#list departments as depts>
            <option value="${depts}">${depts}</option>
          </#list>
        </select><br><br>>
        Course Number:<br>
        <select name="course_number" id="select-course-number">
        </select><br><br>
        Duration:<br>
        <input type="number" min="0" max="24" value="1" class="duration-box" name="duration-hours" id="field-duration-hours"> :
        <input type="number" min="0" max="59" value="30" class="duration-box" name="duration-mins" id="field-duration-mins"><br><br>
        <button id="button-photo-upload">Upload Photo</button><br><br>
      </div>
      <div class="half-column-form">
        Description:<br>
        <input type="text" name="description" id="field-description" placeholder="discussing logical things..."><br><br>
        Building:<br>
        <select name="building" id="select-building">
          <#list buildings as builds>
            <option value="${builds}">${builds}</option>
          </#list>
        </select><br><br>
        Precise Location:<br>
        <input type="text" name="location" id="field-location" placeholder="fifth floor, by the elevator..."><br><br>
        <!-- input type="submit" value="Create Group"></input -->
        <button id="create-group">Create Group</button>
      </div>
    <--/form-->
  </div>
</div>

</div>
</#assign>
<#include "main.ftl">
