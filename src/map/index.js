'use strict'

let map = null
let viewReady = null
let center = null
let zoom = null

function loadMapScenario(){
	console.log('API Ready')
	if( !viewReady && center ){
		loadMap(center)
	}
	viewReady = true
}

function loadMap( center, zoom ){
	zoom || (zoom = 15)
	if( center ){
		map = new Microsoft.Maps.Map(document.getElementById('map'), {
			credentials: 'Am808_tBNMI958nFN95FX2oBKxPNOahsq7vyuUA-ZbPuyiNTUr7L07irajvZl0rh',
			center: center,
			mapTypeId: Microsoft.Maps.MapTypeId.aerial,
			zoom: zoom
		})
		console.log('Load the map on', center, 'with zoom', zoom)
	}
}

function makeMapVisible() {
	$('#loader').toggleClass('hidden', true)
	$('#board').toggleClass('hidden', false)
}

function pointAsLocation( point ){
	return (
		point instanceof Microsoft.Maps.Point
			? map.tryPixelToLocation(point)
			: (point instanceof Microsoft.Maps.Location
					? point
					: new Microsoft.Maps.Location(point.latitude, point.longitude)
				)
	)
}

function locationAsPoint( location ){
	return (
		location instanceof Microsoft.Maps.Location
			? map.tryLocationToPixel( location )
			: (location instanceof Microsoft.Maps.Point
				?	location
				: new Microsoft.Maps.Point(location)
			)
	)
}

function centerMap( point ) {
	if( !map || !point ){
		return
	}
	const target = pointAsLocation( point )
	if( target ){
		map.setView({ center: target, zoom: map.getZoom() })
	}
}

function getInnerBound( bound ) {
	let ret = null
	console.log('Pixel bound', map.tryLocationToPixel(bound))
	const leftPixels = map.tryLocationToPixel(bound.getNorthwest())
	const rightPixels = map.tryLocationToPixel(bound.getSoutheast())
	const widthPercent = map.getWidth() * 0.1
	const heightPercent = map.getHeight() * 0.2
	const rect = {
		leftUp: map.tryPixelToLocation(new Microsoft.Maps.Point(leftPixels.x + widthPercent, leftPixels.y + heightPercent)),
		// rightUp: map.tryPixelToLocation(new Microsoft.Maps.Point(rightPixels.x - widthPercent, leftPixels.y + heightPercent)),
		// leftDown: map.tryPixelToLocation(new Microsoft.Maps.Point(leftPixels.x + widthPercent, rightPixels.y - heightPercent)),
		rightDown: map.tryPixelToLocation(new Microsoft.Maps.Point(rightPixels.x - widthPercent, rightPixels.y - heightPercent))
	}
	return rect
}

function checkPointInBound( point ){
	const rect = getInnerBound(map.getBounds())
	const pixelBound = locationAsPoint(point)
	const leftUp = map.tryLocationToPixel(rect.leftUp)
	const rightDown = map.tryLocationToPixel(rect.rightDown)
	return (
		(leftUp.x <= point.x && point.x <= rightDown.x) &&
		(leftUp.y <= point.y && point.y <= rightDown.y)
	)
}

function drawInnerBound( leftUpCorner, rightDownCorner ){
	const poly = new Microsoft.Maps.Polyline([
		leftUpCorner
		, new Microsoft.Maps.Location(leftUpCorner.latitude, rightDownCorner.longitude)
    , rightDownCorner
    , new Microsoft.Maps.Location(rightDownCorner.latitude, leftUpCorner.longitude)
    , leftUpCorner
  ], { strokeColor: 'red', strokeThickness: 5 })
	map.entities.push(poly)
	return poly
}

function drawPoint( point ) {
	console.log('Draw Point', point)
	const target = pointAsLocation( point )
	const pp = new Microsoft.Maps.Pushpin(target)
	if( !checkPointInBound(point) ){
		centerMap( point )
	}
	map.entities.push(pp)
	return pp
}

function init(){
	makeMapVisible()
	loadMap(new Microsoft.Maps.Location(51.50632, -0.12714))
	return getInnerBound(map.getBounds())
}

$(() => {

	console.log('WebSocket started')	
	const socket = new WebSocket('ws://localhost:8978')

	socket.onmessage = ( message ) => {
		const data = JSON.parse(message.data)
		console.log('Got a message', data)
		const point = new Microsoft.Maps.Location(data.point.latitude, data.point.longitude)
		if( !center ){
			center = point
		}
		if( !viewReady ){
		}else if( !map ){
			makeMapVisible()
			loadMap(center)
			drawPoint(center)
		}else{
			drawPoint(point)
		}
	}

})