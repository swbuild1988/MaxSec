///<reference path='../variable.d.ts'/>

export class CoordMapType {

	private tileSize: any

	constructor(tileSize: any){
		this.tileSize = tileSize
	}
	
	// 控制瓦片的样式，可以不要
	getTile(ownerDocument: any) {
		var div = ownerDocument.createElement('DIV');
		
		div.style.width = this.tileSize.width + 'px';
		div.style.height = this.tileSize.height + 'px';
		div.style.fontSize = '0'; // 设置经纬度的字体大小 
		div.style.borderStyle = 'solid';
		div.style.borderWidth = '0px'; // 设置瓦片边框厚度
		div.style.borderColor = '#AAAAAA'; // 边框的颜色   
		return div;
	};

}
 
export class LocalMapType {

	private tileSize = new google.maps.Size(256, 256)
	/*  地图显示最大级别  */
	private maxZoom: number = 6
	/* 地图显示最小级别  */
	private minZoom: number = 3
	/* 本地按钮，就是离线地图 */
	private name: string = '本地'
	// 显示本地地图可能会出现不清晰的现象
	private alt: string = '显示本地地图'

	getTile(coord: any, zoom: string | number, ownerDocument: any) {
		var img = ownerDocument.createElement("img");	
		img.style.width = this.tileSize.width + "px";
		img.style.height = this.tileSize.height + "px";
		var strURL = "tiles/" + zoom + "/" + coord.x + "/" + coord.y + ".png"; // 访问本地图片的算法，不同的地图切图工具对应的算法不同
		img.src = strURL;
		return img;
	};

}