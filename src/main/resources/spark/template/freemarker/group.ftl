<#assign content>
<div id="group-body">
  <div id="group-centered">
    <div id="group-header">
      <h2>${grouptitle}</h2>
      <h3>${groupclass}</h4>
    </div>
    <div id="group-details">
      <div id="group-detail-size" class="group-detail">
        3 Members
      </div>
      <div id="group-detail-location" class="group-detail">
        Sciences Library Basement, Room 101
      </div>
      <div id="group-detail-time" class="group-detail">
        2 hr 12 min remaining
      </div>
    </div>
    <hr>
    <div id="group-description">
      <span style="bold">Description: </span>${groupdesc}
    </div>
    <div id="group-members">
      <#list groupemails as email>
		${email}
	  </#list>
    </div>
    <div id="group-options">
      <div id="group-option-invisible" class="group-option">
        invisible
      </div>
      <div id="group-option-extend" class="group-option">
        <button id="extend-button">EXTEND TIME</button>
      </div>
      <div id="group-option-end" class="group-option">
        <button id="end-button">END TIME</button>
      </div>
    </div>
  </div>
</div>
</#assign>
<#include "main.ftl">

