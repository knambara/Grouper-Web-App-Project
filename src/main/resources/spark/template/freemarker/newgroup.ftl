<#assign content>
<div id="newgroup-body">

  <div id="newgroup-content">
    <h1>New Group</h1>
    <p id="incomplete-form-error"></p>
    <!-- form id="new-group-form" method="POST" -->

    <div id="new-group-form">

      <div class="half-column-form">
        <p>Department:</p>
        <select name="department" id="select-department">
          <#list departments as depts>
            <option value="${depts}">${depts}</option>
          </#list>
        </select>

        <p>Course Number:</p>
        <select name="course_number" id="select-course-number">
        </select>

        <p>Duration:</p>
        <div id="duration-select">
        <input type="number" min="0" max="23" value="1" class="duration-box" name="duration-hours" id="field-duration-hours"> hr
        <input type="number" min="0" max="59" value="30" class="duration-box" name="duration-mins" id="field-duration-mins"> min
        </div>
        <!-- <button id="button-photo-upload">Upload Photo</button> -->
      </div>

      <div class="half-column-form">
        <p>Title:</p>
        <input type="text" name="grouptitle" id="field-title" placeholder="Studying for ...">

        <p>Description:</p>
        <!--<input type="text" name="description" id="field-description" placeholder="discussing logical things...">-->
        <textarea name="description" id="field-description" placeholder="Describe what your group is up to!" rows=4></textarea>

        <p>Building:</p>
        <select name="building" id="select-building">
          <#list buildings as builds>
            <option value="${builds}">${builds}</option>
          </#list>
        </select>

        <p>Precise Location:</p>
        <input type="text" name="location" id="field-location" placeholder="fifth floor, by the elevator...">

        <!-- input type="submit" value="Create Group"></input -->
        <button id="create-group">Create Group</button>
      </div>
    <!--/form-->
  </div>
</div>

</div>
</#assign>
<#include "main.ftl">

<script>
$(document).ready(function() {
  console.log("newgroup");
  setup_live_groups();
});
</script>
