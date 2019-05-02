<#assign content>
<div id="group-body">
<div id="group-centered">
<div id="back-button">
<button id="back-to-dashboard">Back to Dashboard</button>
</div>
  <div id="group-header">
    <h2>${grouptitle}</h2>
    <h3>${groupclass}</h4>
  </div>
  <div id="group-details">
    <div id="group-detail-size" class="group-detail">
      <img width="20" src="/img/person-icon.png"/>
      <div id="groupSize">${groupSize}</div> <div> Member(s)</div>
    </div>
    <div id="group-detail-location" class="group-detail">
      <div><img width="25" src="/img/location-icon.png"/></div>
      <div><p id="building">${groupbuilding}</p>
      <p id="spec-loc">${grouproom}</p></div>
    </div>
    <div id="group-detail-time" class="group-detail">
      <div><img width="25" src="/img/clock-icon.png"/></div>
      <div id="group-duration">${groupduration}</div>
      <p id="end-time">${groupendtime}</p>
    </div>
  </div>
  <hr>
  <div id="group-description">
    <div id="description-tag">Description:</div>
    <div id="description-details">${groupdesc}</div>
  </div>
  <div id="group-members">
    <#list groupusers as user>
      <p id=${user.getEmail()}>${user.getEmail()}</p>
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
