package com.sky.cookbooksa.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * 
 * @author arvin.li
 *
 */
public class Region {

	private int regionId; 
	private int regionCode;
	private String regionName;
	private int parentId;
	private int regionLevel;
	private int regionOrder;
	private String regionEnName;
	private String regionShortEnName;

	public int getRegionId() {
		return regionId;
	}
	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}
	public int getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(int regionCode) {
		this.regionCode = regionCode;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getRegionLevel() {
		return regionLevel;
	}
	public void setRegionLevel(int regionLevel) {
		this.regionLevel = regionLevel;
	}
	public int getRegionOrder() {
		return regionOrder;
	}
	public void setRegionOrder(int regionOrder) {
		this.regionOrder = regionOrder;
	}
	public String getRegionEnName() {
		return regionEnName;
	}
	public void setRegionEnName(String regionEnName) {
		this.regionEnName = regionEnName;
	}
	public String getRegionShortEnName() {
		return regionShortEnName;
	}
	public void setRegionShortEnName(String regionShortEnName) {
		this.regionShortEnName = regionShortEnName;
	}

	/**
	 * Region按拼音排序
	 * @param regionList
	 */
	public static void sortRegionList(ArrayList<Region> regionList){

		Collections.sort(regionList, new Comparator<Region>() {

			@Override
			public int compare(Region arg0, Region arg1) {
				// TODO Auto-generated method stub
				return arg0.getRegionEnName().compareTo(arg1.getRegionEnName());
			}
		});
	}

}
