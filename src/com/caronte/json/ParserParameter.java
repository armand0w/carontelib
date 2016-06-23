package com.caronte.json;

public class ParserParameter 
{
	private char[] array;
	private int pivot;
	private int length;
	
	public ParserParameter(char[] array) {
		this.pivot = 0;
		this.length = array.length;
		this.array = array;
	}
	
	public char getNext(){
		return this.array[this.pivot++];
	}

	public boolean hasNext(){
		return pivot < length;
	}
	
	public int getLength() {
		return length;
	}
	
	public String SkipNumber(){
		StringBuffer buffer = new StringBuffer();
		
		while(pivot < length){
			if (array[pivot] >= '0' && array[pivot] <= '9'){
				buffer.append(array[pivot]);
				pivot++;
				continue;
			}			
			break;
		}
		
		return buffer.toString();
	}
	
	public String SkipAlphabet(){
		StringBuffer buffer = new StringBuffer();
		
		while((pivot) < length){
			if (array[pivot] >= 'a' && array[pivot] <= 'z'){
				buffer.append(array[pivot]);
				pivot++;
				continue;
			}			
			if (array[pivot] >= 'A' && array[pivot] <= 'Z'){
				buffer.append(array[pivot]);
				pivot++;
				continue;
			}			
			if (array[pivot] >= '0' && array[pivot] <= '9'){
				buffer.append(array[pivot]);
				pivot++;
				continue;
			}			
			if (array[pivot] == '+' || array[pivot] == '-' || array[pivot] == '*' || array[pivot] == '/'){
				buffer.append(array[pivot]);
				pivot++;
				continue;
			}			
			break;
		}
		
		return buffer.toString();
	}
}
