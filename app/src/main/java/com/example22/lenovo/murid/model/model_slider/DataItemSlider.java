package com.example22.lenovo.murid.model.model_slider;


import com.google.gson.annotations.SerializedName;


public class DataItemSlider {

	@SerializedName("id_slider")
	private String idSlider;

	@SerializedName("judul_slider")
	private String judulSlider;

	@SerializedName("gbr_slider")
	private String gbrSlider;

	@SerializedName("desc_slider")
	private String descSlider;

	public void setIdSlider(String idSlider){
		this.idSlider = idSlider;
	}

	public String getIdSlider(){
		return idSlider;
	}

	public void setJudulSlider(String judulSlider){
		this.judulSlider = judulSlider;
	}

	public String getJudulSlider(){
		return judulSlider;
	}

	public void setGbrSlider(String gbrSlider){
		this.gbrSlider = gbrSlider;
	}

	public String getGbrSlider(){
		return gbrSlider;
	}

	public void setDescSlider(String descSlider){
		this.descSlider = descSlider;
	}

	public String getDescSlider(){
		return descSlider;
	}

	@Override
 	public String toString(){
		return 
			"DataItemSlider{" +
			"id_slider = '" + idSlider + '\'' + 
			",judul_slider = '" + judulSlider + '\'' + 
			",gbr_slider = '" + gbrSlider + '\'' + 
			",desc_slider = '" + descSlider + '\'' + 
			"}";
		}
}