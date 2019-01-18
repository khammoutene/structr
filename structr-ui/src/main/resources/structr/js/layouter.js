/*
 * Copyright (C) 2010-2018 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */

class Layouter {

	constructor(container) {

		if (new .target === Layouter) {
			throw new TypeError("Cannot construct Layouter instances directly, please use one of the subclasses.");
		}

		this.container = container;
	}

	/**
	 * Notify layouter that nodes and edges will be added.
	 * @returns {undefined}
	 */
	begin() {
	}

	/**
	 * Adds a node with the given name.
	 * 
	 * @param {string} id
	 * @param {string} name
	 * 
	 * @returns the newly created node object
	 */
	addNode(id, name) {
	}

	/**
	 * Adds an edge with the given name between source and target.
	 * 
	 * @param {string} id
	 * @param {string} name
	 * @param {Node} source
	 * @param {Node} target
	 * 
	 * @returns the newly created edge object
	 */
	addEdge(id, name, source, target) {
	}

	/**
	 * Notify layouter that nodes and edges have been added.
	 * @returns {undefined}
	 */
	end() {
	}

	layout() {
	}

	update(key, value) {
	}
}
;

class VISLayouter extends Layouter {

	constructor(container) {

		super(container);

		this.data = {
			nodes: [],
			edges: []
		};
		this.cache = {};
		this.options = {

			nodes: {
				shape: 'box',
				widthConstraint: {
					minimum: 80
				},
				margin: {
					top: 2,
					bottom: 1,
					left: 20,
					right: 20
				},
				shapeProperties: {
					borderRadius: 10
				},
				font: {
					multi: 'html',
					color: '#999999',
					size: 10,
					bold: '10px roboto #333333'
				}
			},
			edges: {
				arrows: {
					to: {
						enabled: true,
						scaleFactor: 0.6,
						type: 'arrow'
					},
					middle: {
						enabled: false,
						scaleFactor: 1,
						type: 'arrow'
					},
					from: {
						enabled: true,
						scaleFactor: 0.05,
						type: 'circle'
					}
				},
				width: 2,
				arrowStrikethrough: false,
				font: {
					size: 8
				},
				smooth: {
					type: 'discrete',
					roundness: 0.9
				}
			},
			interaction: {
				dragNodes: false,
				multiselect: true
			},
			physics: {
				enabled: true,
				forceAtlas2Based: {
					gravitationalConstant: -300,
					centralGravity: 0.02,
					springConstant: 0.5,
					springLength: 100,
					damping: 0.4,
					avoidOverlap: 1
				},
				maxVelocity: 50,
				minVelocity: 0.1,
				solver: 'forceAtlas2Based',
				stabilization: {
					enabled: true,
					iterations: 1000,
					updateInterval: 100,
					onlyDynamicEdges: false,
					fit: true
				},
				timestep: 0.5,
				adaptiveTimestep: true
			},
			layout: {
				improvedLayout: true,
				randomSeed: 1234
			},
			manipulation: {
				enabled: false,
				addEdge: function (data, callback) {
					console.log('add edge', data);
				}
			}
		}
	}

	begin() {
	}

	addNode(id, name) {

		var node = {
			id: id,
			label: name,
			color: {
				background: '#eeeeee',
				border: '#eeeeee'
			}
		};

		this.data.nodes.push(node);
		return id;
	}

	addEdge(id, name, source, target, active) {

		var key = source + '.' + target;
		if (this.cache[key]) {
			return;
		}

		this.cache[key] = true;

		var edge = {
			id: id,
			from: source,
			to: target,
			label: name,
			color: {
				color: active ? '#81ce25' : '#666666',
				opacity: active ? 1.0 : 0.1
			},
			physics: true,
			dashes: !active
		};

		this.data.edges.push(edge);
		return id;
	}

	end() {
		this.network = new vis.Network(this.container, this.data, this.options);
		$(document).on('keydown', (event) => {
			if (event.key === 'Shift') {
				this.network.addEdgeMode();
				this.network.setOptions({
					interaction: {
						hover: true
					}
				});
				this.network.stopSimulation();
			}
		});
		$(document).on('keyup', (event) => {
			if (event.key === 'Shift') {
				this.network.disableEditMode();
				this.network.setOptions({
					interaction: {
						hover: false
					}
				});
				this.network.stopSimulation();
			}
		});
	}

	layout() {
		window.setTimeout(() => {
			this.network.stopSimulation();
		}, 1000);
	}

	update(key, value) {

		switch (key) {
			case 'roundness':
				this.network.setOptions({
					edges: {
						smooth: {
							roundness: value
						}
					}
				});
				window.setTimeout(() => {
					this.network.stopSimulation();
				}, 100);
				break;
			case 'gravity':
				this.network.setOptions({
					physics: {
						forceAtlas2Based: {
							gravitationalConstant: -value
						}
					}
				});
				window.setTimeout(() => {
					this.network.stopSimulation();
				}, 100);
				break;
		}
	}

	on(event, func) {
		this.network.off(event).on(event, func);
	}

	focus(id) {
		this.network.focus(id, {
			locked: false,
			scale: 2.0,
			animation: {
				duration: 300,
				easingFunction: 'easeInOutQuad'
			}
		});
	}

	test() {
		this.network.addEdgeMode();
		this.network.disableEditMode();
	}
}