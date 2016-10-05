var data;
processData = function(response) {
    data = JSON.parse(response);
};
jQuery.get( "../clustering", processData);
var pc = d3.parcoords()("#graphwindow")
    .data(data)
    .render()
    .createAxes();