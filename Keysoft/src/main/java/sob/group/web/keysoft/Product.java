package sob.group.web.keysoft;

import java.util.ArrayList;
import java.util.List;

public class Product {
	
	private String section;
	private String barcode;
	private String name;
	private double price;
	private int per;
	private int cartonSize;
	
	public Product(String section,String barcode,String name,double price,int per,int cartonSize) {
		this.section=section;
		this.barcode=barcode;
		this.name=name;
		this.price=price;
		this.per=per;
		this.cartonSize=cartonSize;
	}
	
	public Product(List<String> list) {
		this.section=list.get(0);
		this.barcode=list.get(1);
		this.name=list.get(2);
		this.price=Double.parseDouble(list.get(3));
		this.per=Integer.parseInt(list.get(4));
		this.cartonSize=Integer.parseInt(list.get(5));
	}
	
	public static List<Product> makeProductList(List<String> sectionList){
		List<Product> result = new ArrayList<Product>();
    	for(String s:sectionList) {
    		result.add(new Product(Product.formatEntry(s)));
    	}
		return result;
	}
		
	public static List<String> formatEntry(String s) {
		List<String> result = new ArrayList<String>();
		String[] split = s.split("\\s+");
		String amount = split[split.length-1];
		String per = split[split.length-2];
		String price = split[split.length-3];
		String section = split[0];
		String barcode = split[1];
		String namevalue = "";
		for(int i = 2;i < split.length-3;i++) {
			namevalue+= split[i] + " ";
		}
		String name = namevalue.trim();
		result.add(section);
		result.add(barcode);
		result.add(name);
		result.add(price);
		result.add(per);
		result.add(amount);
		return result;
	}
	
	public void print() {
		System.out.println(this.toString());
	}

	@Override
	public String toString() {
		return section + " " + barcode + " " + name + " " + price + " " + per + " " + cartonSize;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getPer() {
		return per;
	}

	public void setPer(int per) {
		this.per = per;
	}

	public int getCartonSize() {
		return cartonSize;
	}

	public void setCartonSize(int cartonSize) {
		this.cartonSize = cartonSize;
	}
}
