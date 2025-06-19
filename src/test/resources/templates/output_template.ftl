{
  "OrderNo": "${order.orderNumber}",
  "OrderDate": "${order.processed}",
  "Release": ${order.release?c},
  "OrderLines": [
    <#list order.lines as line>
      {
        "unit": "${line.unit}",
        "quantity": ${line.quantity},
        "itemNumber": "${line.itemNumber}"
      }<#if line?has_next>,</#if>
    </#list>
  ],
  "ExtraInfo": "${extraInfo}",
  "PluginField": "${pluginAdded}"
}
