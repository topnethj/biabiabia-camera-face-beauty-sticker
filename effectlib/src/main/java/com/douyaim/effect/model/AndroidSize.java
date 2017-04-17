package com.douyaim.effect.model;

public class AndroidSize {

	public AndroidSize() {
		width = 0;
		height = 0;
	}
	
	public AndroidSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AndroidSize other = (AndroidSize) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	public int width;
	public int height;
}