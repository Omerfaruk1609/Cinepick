package proje.cinepick.entites;
import java.time.LocalDateTime; 

public abstract class Assest {
    private int id;
    private String name;
    private LocalDateTime createdDate; 
    private String status;

    public Assest(int id, String name, LocalDateTime createdDate, String status) {
		super();
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Assest() {
        
        this.createdDate = LocalDateTime.now(); 
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	 
    
}
