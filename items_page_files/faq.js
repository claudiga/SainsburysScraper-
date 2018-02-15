require(
	["dojo/dom", "dojo/fx", "dojo/query", "dojo/on" , "dojo/NodeList-traverse","dojo/dom-style","dojo/domReady!"],
	function(dom, fx, query, on, traverse, style) {

		function toggle(faq_action_node) {
			var nodeList = query.NodeList();
			nodeList.push(faq_action_node);
			var faq = nodeList.parent().parent().parent().parent()[0];
			var faq_title = query(".es-faq-accordion-title",faq)[0];
			var faq_body = query(".es-faq-accordion-body",faq)[0];
			if ("none" == (style.get(faq_body,"display"))) {
				expand(faq_title, faq_body, 500);
			} else {
				collapse(faq_title, faq_body, 500);
			}
		}

		function expand(faq_title, faq_body, duration) {
			fx.wipeIn({
				node: faq_body, duration: duration, onEnd: function() {
					var nodeList = query.NodeList();
					nodeList.push(faq_title);
					var grandParent = nodeList.parent().parent()[0];
					dojo.forEach(query(".es-faq-accordion-title-link",faq_title), function(item) {
						item.innerHTML=grandParent.getAttribute("es-faq-accordion-header-text-selected");
					});
					dojo.forEach(query(".es-faq-accordion-title-image img",faq_title), function(item) {
						item.src=grandParent.getAttribute("es-faq-accordion-arrow-selected-image");
						item.alt=grandParent.getAttribute("es-faq-accordion-arrow-selected-image-alt");
						item.title=grandParent.getAttribute("es-faq-accordion-arrow-selected-image-alt");
					});
					nodeList.style("backgroundImage", "url(" + grandParent.getAttribute("es-faq-accordion-header-selected-image") + ")"); 
				}
			}).play();
		}
	
		function collapse(faq_title, faq_body, duration) {
			fx.wipeOut({
					node: faq_body, duration: duration, onEnd: function() {
						var nodeList = query.NodeList();
						nodeList.push(faq_title);
						var grandParent = nodeList.parent().parent()[0];
						dojo.forEach(query(".es-faq-accordion-title-link",faq_title), function(item) {
							item.innerHTML=grandParent.getAttribute("es-faq-accordion-header-text-nonselected");
						});
						dojo.forEach(query(".es-faq-accordion-title-image img",faq_title), function(item) {
							item.src=grandParent.getAttribute("es-faq-accordion-arrow-non-selected-image");
							item.alt=grandParent.getAttribute("es-faq-accordion-arrow-non-selected-image-alt");
							item.title=grandParent.getAttribute("es-faq-accordion-arrow-non-selected-image-alt");
						});
						nodeList.style("backgroundImage", "url(" + grandParent.getAttribute("es-faq-accordion-header-non-selected-image") + ")"); 
					}
			}).play();
		}

		var faq_bodies = query("div.es-faq-accordion-body");
		for (var i=0; i < faq_bodies.length; i++) {
			var node=faq_bodies[i];
			var nodeList = query.NodeList();
			nodeList.push(node);
			var faq_title = query(".es-faq-accordion-title",nodeList.parent()[0])[0];
			collapse(faq_title, node, 0);
			dojo.forEach(
				query("div.es-faq-accordion-title-link, div.es-faq-accordion-title-image", faq_title),
				function(item) {
					on(item, "click", function(evt) {
							toggle(evt.target); /* expand/collapse the faq on click */
						}
					);
					style.set(item,"cursor","pointer");
				}
			);
		}
	}
);