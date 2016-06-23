package com.caronte.db;

import java.util.ArrayList;

public class PageScroller 
{
	public static ArrayList<Integer> create(Integer maxScrollElements, Integer dataSize, Integer pageSize, Integer currentPage)
	{
		Integer totalPages;
		ArrayList<Integer> pageScroller = new ArrayList<Integer>();
		int i;
		
		if (maxScrollElements % 2 == 0)
		{
			maxScrollElements++;
		}
		
		totalPages = (dataSize / pageSize) + ((dataSize % pageSize != 0)?1:0);
		
		if (totalPages <= maxScrollElements)
		{
			for (i = 0; i < totalPages; i++)
			{
				pageScroller.add(new Integer(i + 1));
			}
			
			return pageScroller;
		}
		 
		if (currentPage < (maxScrollElements - 4))
		{
			for (i = 0; i < (maxScrollElements - 2); i++)
			{
				pageScroller.add(new Integer(i + 1));
			}
			
			pageScroller.add(null);
			pageScroller.add(totalPages);
			
			return pageScroller;
		}
		 
		if (((totalPages - currentPage) + 1) < (maxScrollElements - 4))
		{
			pageScroller.add(new Integer(1));
			pageScroller.add(null);
			
			for (i = (totalPages - (maxScrollElements - 3)); i < totalPages; i++)
			{
				pageScroller.add(new Integer(i + 1));
			}
			
			return pageScroller;
		}

		pageScroller.add(new Integer(1));
		pageScroller.add(null);
		
		for (i = 0; i < (maxScrollElements - 4); i++)
		{
			pageScroller.add(new Integer(i + (currentPage - ((maxScrollElements - 4) / 2))));
		}
				
		pageScroller.add(null);
		pageScroller.add(totalPages);
		
		return pageScroller;
	}
	
	public static void main(String[] args) 
	{
		for (int i = 1; i <= 35; i++)
		{
			ArrayList<Integer> pageScroller = PageScroller.create(12, 1734, 50, i);
			
			for (Integer integer : pageScroller) 
			{
				System.out.print(integer + " ");
			}
			
			System.out.println("");
		}
	}
}
