/*
 *  Copyright (C) 2011 Axel Morgner
 *
 *  This file is part of structr <http://structr.org>.
 *
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */

var resources;
var previews;

$(document).ready(function() {
	Structr.registerModule('resources', Resources);
});

var Resources = {
	
	init : function() {
		//alert('Resouces.init()');
	},

	onload : function() {
		//Structr.activateMenuEntry('resources');
		if (debug) console.log('onload');
		main.append('<table><tr><td id="resources"></td><td id="previews"></td></tr></table>');
		resources = $('#resources');
		previews = $('#previews');
		Resources.show();
	},

	show : function() {
		//return Entities.showEntities('Resource');

		$.ajax({
			url: rootUrl + 'resources',
			dataType: 'json',
			contentType: 'application/json; charset=utf-8',
			//headers: { 'X-User' : 457 },
			success: function(data) {
				if (!data || data.length == 0 || !data.result) return;
				if ($.isArray(data.result)) {
        
					for (var i=0; i<data.result.length; i++) {
						var resource = data.result[i];

						Entities.appendEntityElement(resource);

						var resourceId = resource.id;
						//          $('#resources').append('<div class="editor_box"><div class="nested top resource" id="resource_' + id + '">'
						//                              + '<b>' + resource.name + '</b>'
						//                              //+ ' [' + id + ']'
						//                              + '<img class="add_icon button" title="Add Element" alt="Add Element" src="icon/add.png" onclick="addElement(' + id + ', \'#resource_' + id + '\')">'
						//                              + '<img class="delete_icon button" title="Delete '
						//                              + resource.name + '" alt="Delete '
						//                              + resource.name + '" src="icon/delete.png" onclick="deleteNode(' + id + ', \'#resource_' + id + '\')">'
						//                              + '</div></div>');
						Resources.showSubEntities(resourceId, null);
      
						$('#previews').append('<a target="_blank" href="' + viewRootUrl + resource.name + '">' + viewRootUrl + resource.name + '</a><br><div class="preview_box"><iframe id="preview_'
							+ resourceId + '" src="' + viewRootUrl + resource.name + '?edit"></iframe></div><div style="clear: both"></div>');
      
						$('#preview_' + resourceId).load(function() {
							//console.log(this);
							var doc = $(this).contents();
							var head = $(doc).find('head');
							head.append('<style type="text/css">'
								+ '.structr-editable-area {'
								+ 'border: 1px dotted #a5a5a5;'
								+ 'margin: 2px;'
								+ 'padding: 2px;'
								+ '}'
								+ '.structr-editable-area-active {'
								+ 'border: 1px dotted #orange;'
								+ 'margin: 2px;'
								+ 'padding: 2px;'
								+ '}'
								+ '</style>');


							$(this).contents().find('.structr-editable-area').each(function(i,element) {
								//console.log(element);
								$(element).addClass('structr-editable-area');
								$(element).on({
									mouseenter: function() {
										var self = $(this);
										self.attr('contenteditable', true);
										self.addClass('structr-editable-area-active');
									},
									mouseleave: function() {
										var self = $(this);
										self.attr('contenteditable', true);
										self.removeClass('structr-editable-area-active');
										var id = self.attr('id');
										id = lastPart(id, '-');
										Resources.updateContent(id, this.innerHTML);
									}
								});
							});
						});
      
					}
				}
			}
		});
	},

	updateContent : function(contentId, content) {
		//console.log('update ' + contentId + ' with ' + content);
		var url = rootUrl + 'content' + '/' + contentId;
		var data = '{ "content" : ' + $.quoteString(content) + ' }';
		$.ajax({
			url: url,
			//async: false,
			type: 'PUT',
			dataType: 'json',
			contentType: 'application/json; charset=utf-8',
			data: data,
			success: function(data) {
			//refreshIframes();
			//keyEventBlocked = true;
			//enable(button);
			//console.log('success');
			}
		});
	},

	addResource : function() {
		//var pos = $('#group_' + groupId + ' > div.nested').length;
		//console.log('addNode(' + type + ', ' + resourceId + ', ' + elementId + ', ' + pos + ')');
		var url = rootUrl + 'resource';
		var data = '{ "type" : "Resource", "name" : "resource_' + Math.floor(Math.random() * (9999 - 1)) + '" }';
		//console.log(data);
		var resp = $.ajax({
			url: url,
			//async: false,
			type: 'POST',
			dataType: 'json',
			contentType: 'application/json; charset=utf-8',
			data: data,
			success: function(data) {
				//            var nodeUrl = resp.getResponseHeader('Location');
				//console.log(nodeUrl);
				//setPosition(groupId, nodeUrl, pos);
				refreshMain();
			}
		});
	},

	showSubEntities : function(resourceId, entity) {
		var follow = followIds(resourceId, entity);
		$(follow).each(function(i, nodeId) {
			if (nodeId) {
				//            console.log(rootUrl + nodeId);
				$.ajax({
					url: rootUrl + nodeId,
					dataType: 'json',
					contentType: 'application/json; charset=utf-8',
					async: false,
					//headers: { 'X-User' : 457 },
					success: function(data) {
						if (!data || data.length == 0 || !data.result) return;
						var result = data.result;
						//                    console.log(result);
						Resources.appendElement(result, entity, resourceId);
						Resources.showSubEntities(resourceId, result);
					}
				});
			}
		});
	},

	appendElement : function(entity, parentEntity, resourceId) {
		//    console.log('appendElement: resourceId=' + resourceId);
		//    console.log(entity);
		//    console.log(parentEntity);
		var type = entity.type.toLowerCase();
		var id = entity.id;
		var resourceEntitySelector = $('.' + resourceId + '_');
		var element = (parentEntity ? $('.' + parentEntity.id + '_', resourceEntitySelector) : resourceEntitySelector);
		//    console.log(element);
		Entities.appendEntityElement(entity, element);

		if (type == 'content') {
			div.append('<img title="Edit Content" alt="Edit Content" class="edit_icon button" src="icon/pencil.png">');
			$('.edit_icon', div).on('click', function() {
				editContent(this, resourceId, id)
			});
		} else {
			div.append('<img title="Add" alt="Add" class="add_icon button" src="icon/add.png">');
			$('.add_icon', div).on('click', function() {
				addNode(this, 'content', entity, resourceId)
			});
		}
		//    //div.append('<img class="sort_icon" src="icon/arrow_up_down.png">');
		div.sortable({
			axis: 'y',
			appendTo: '.' + resourceId + '_',
			delay: 100,
			containment: 'parent',
			cursor: 'crosshair',
			//handle: '.sort_icon',
			stop: function() {
				$('div.nested', this).each(function(i,v) {
					var nodeId = getIdFromClassString($(v).attr('class'));
					if (!nodeId) return;
					var url = rootUrl + nodeId + '/' + 'in';
					$.ajax({
						url: url,
						dataType: 'json',
						contentType: 'application/json; charset=utf-8',
						async: false,
						headers: headers,
						success: function(data) {
							if (!data || data.length == 0 || !data.result) return;
							//                        var rel = data.result;
							//var pos = rel[parentId];
							var nodeUrl = rootUrl + nodeId;
							setPosition(resourceId, nodeUrl, i)
						}
					});
					refreshIframes();
				});
			}
		});
	},


	addNode : function(button, type, entity, resourceId) {
		if (isDisabled(button)) return;
		disable(button);
		var pos = $('.' + resourceId + '_ .' + entity.id + '_ > div.nested').length;
		//    console.log('addNode(' + type + ', ' + entity.id + ', ' + entity.id + ', ' + pos + ')');
		var url = rootUrl + type;
		var resp = $.ajax({
			url: url,
			//async: false,
			type: 'POST',
			dataType: 'json',
			contentType: 'application/json; charset=utf-8',
			headers: headers,
			data: '{ "type" : "' + type + '", "name" : "' + type + '_' + Math.floor(Math.random() * (9999 - 1)) + '", "elements" : "' + entity.id + '" }',
			success: function(data) {
				var getUrl = resp.getResponseHeader('Location');
				$.ajax({
					url: getUrl + '/all',
					success: function(data) {
						var node = data.result;
						if (entity) {
							Resources.appendElement(node, entity, resourceId);
							Resources.setPosition(resourceId, getUrl, pos);
						}
						//disable($('.' + groupId + '_ .delete_icon')[0]);
						enable(button);
					}
				});
			}
		});
	}

};