package com.example22.lenovo.murid.model.model_slider;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseSlider{

	@SerializedName("pesan")
	private String pesan;

	@SerializedName("data")
	private List<DataItemSlider> data;

	@SerializedName("status")
	private boolean status;

	public void setPesan(String pesan){
		this.pesan = pesan;
	}

	public String getPesan(){
		return pesan;
	}

	public void setData(List<DataItemSlider> data){
		this.data = data;
	}

	public List<DataItemSlider> getData(){
		return data;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"ResponseSlider{" + 
			"pesan = '" + pesan + '\'' + 
			",data = '" + data + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}