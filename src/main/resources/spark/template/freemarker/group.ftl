<#assign content>
<div id="group-body">
  <div id="group-centered">
    <div id="group-header">
      <h2>${grouptitle}</h2>
      <h3>${groupclass}</h4>
    </div>
    <div id="group-details">
      <div id="group-detail-size" class="group-detail">
        <img width="20" src="/img/person-icon.png"/>
        <div id="groupSize">${groupSize}</div> Members
      </div>
      <div id="group-detail-location" class="group-detail">
        <div><img width="25" src="/img/location-icon.png"/></div>
        <div><p id="building">Science Library</p>
        <p id="spec-loc">Basement, Room A23</p></div>
      </div>
      <div id="group-detail-time" class="group-detail">
        <img width="25" src="/img/clock-icon.png"/>
        2 hr 12 min remaining
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
      <div id="group-option-invisible" class="group-option">
        <p id="invisible"> INVISIBLE </p>
        <label class="switch">
            <input type="checkbox">
            <span class="slider round"></span>
        </label>
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
