<#assign content>
<div id="group-body">
  <div id="group-centered">
    <div id="group-header">
      <h2>${grouptitle}</h2>
      <h3>${groupclass}</h4>
    </div>
    <div id="group-details">
      <div id="group-detail-size" class="group-detail">
        ${groupSize} Members
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
      <#list groupusers as user>
		<p>${user.getEmail()}</p>
	  </#list>
    </div>
    <div id="group-options">      
      <div id="group-option-leave" class="group-option">
        <button id="leave-button">LEAVE</button>
      </div>
    </div>
  </div>
</div>
</#assign>
<#include "main.ftl">

