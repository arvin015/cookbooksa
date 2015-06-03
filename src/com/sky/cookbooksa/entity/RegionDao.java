package com.sky.cookbooksa.entity;

import java.util.ArrayList;

import com.sky.cookbooksa.utils.AssetsDatabaseManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * 
 * @author arvin.li
 *
 */
public class RegionDao {

	/**
	 * 根据ParentId获取Region集合
	 * @param parentId
	 * @return
	 */
	public ArrayList<Region> quaryRegionListByParentId(int parentId){

		ArrayList<Region> regionList = new ArrayList<Region>();

		SQLiteDatabase db = AssetsDatabaseManager.getManager().getDatabase("region.db");

		Cursor cursor = db.rawQuery("select * from region where PARENT_ID=?", new String[]{parentId + ""});

		if(cursor != null){
			cursor.moveToFirst();

			for(int i = 0; i < cursor.getCount(); i++){

				int id = cursor.getInt(0);
				int code = cursor.getInt(1);
				String name = cursor.getString(2);
				int pId = cursor.getInt(3);
				int level = cursor.getInt(4);
				int order = cursor.getInt(5);
				String enName = cursor.getString(6);
				String shortEnName = cursor.getString(7);

				Region region = new Region();
				region.setRegionId(id);
				region.setRegionCode(code);
				region.setRegionName(name);
				region.setParentId(pId);
				region.setRegionLevel(level);
				region.setRegionOrder(order);
				region.setRegionEnName(enName);
				region.setRegionShortEnName(shortEnName);

				regionList.add(region);

				cursor.moveToNext();

			}
		}

		Region.sortRegionList(regionList);

		return regionList;
	}
}
