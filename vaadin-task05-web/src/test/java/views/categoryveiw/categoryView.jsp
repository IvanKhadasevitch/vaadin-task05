
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

</head>
<body>
<%-- Category Menu button --%>
<span tabindex="-1" class="v-menubar-menuitem v-menubar-menuitem-unchecked" role="menuitem">
    <span class="v-menubar-menuitem-caption">
        <span class="v-icon Vaadin-Icons"></span>Category
    </span>
</span>

<%-- Add category: Switch ON button --%>
<div tabindex="0" role="button" class="v-button v-widget small v-button-small" id="addCategoryBtnCategoryView_id">
    <span class="v-button-wrap">
        <span class="v-button-caption">Add category</span>
    </span>
</div>

<%-- Delete category: Switch OFF button--%>
<span class="v-button-caption">Delete category</span>

<%-- Category Grid row teg: tr --%>
<tr class="v-grid-row v-grid-row-has-data v-grid-row-focused" role="row" aria-selected="false" style="width: 500px; transform: translate3d(0px, 228px, 0px);">
    <td class="v-grid-cell frozen last-frozen" role="gridcell" colspan="1" style="height: 38px; width: 57px; transform: translate3d(0px, 0px, 0px);">
        <span class="v-grid-selection-checkbox v-assistive-device-only-label" style="">
            <input type="checkbox" value="on" id="gwt-uid-9" tabindex="0">
            <label for="gwt-uid-9">Selects row number 8.</label>
        </span>
    </td>
    <td class="v-grid-cell v-grid-cell-focused" role="gridcell" colspan="1" style="height: 38px; width: 443px;">SeleniumCategory1</td>
</tr>

<input type="checkbox" value="on" id="gwt-uid-3" tabindex="0">

<%-- base Menu --%>
<div tabindex="0" class="v-menubar v-widget borderless v-menubar-borderless" role="menubar">
    <span tabindex="-1" class="v-menubar-menuitem v-menubar-menuitem-unchecked" role="menuitem">
        <span class="v-menubar-menuitem-caption"><span class="v-icon Vaadin-Icons"></span>
            Hotel
        </span>
    </span>
    <span tabindex="-1" class="v-menubar-menuitem v-menubar-menuitem-checked" role="menuitem">
        <span class="v-menubar-menuitem-caption"><span class="v-icon Vaadin-Icons"></span>
            Category
        </span>
    </span>
</div>

</body>
</html>
