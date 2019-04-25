<#assign content>
<script>
  // This page is sent if a login attempt has the wrong hash.
  localStorage.removeItem("grouper_email");
  localStorage.removeItem("grouper_hash");
  window.location.href = "/grouper";
</script>
</#assign>
<#include "main.ftl">
