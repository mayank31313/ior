package ai.mayank.iot.tables;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "user")
public class User {
		@Id
		@GeneratedValue(strategy= GenerationType.AUTO)
	   @Column(name="id",updatable=false,nullable=false)
	   private Long id;
	   
		@Column(name="pub_sub_pass")
		private String pubPass;
		
		public void setPubPass(String f) {
			if(this.pubPass == null)
				this.pubPass = f;
		}
		
		public String getPubPass() {
			return this.pubPass;
		}
		
		@Column(nullable=false)
		private Boolean isIFTTT;
		
	   @Column(name = "uuid")
	   private String uuid;
	   
	   @Column(name="auth_key")
	   private String key;
	   
	   @Column(name = "pub_sub")
	   private boolean pubsubenabled;
	   
	   public void setPubSubEnabled(boolean f) {
		   this.pubsubenabled = f;
	   }
	   public boolean getPubSubEnabled() {
		   return this.pubsubenabled;
	   }
	   
	   	@OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
		@JoinColumn(name="user_id")
		private Set<EndpointHits> hits;
		
	   public Long getId() {
		return id;
	   }
	
		public void setId(Long id) {
			this.id = id;
		}
	
	   @Column(name = "name")
	   private String name;
	   
	   @Column(name = "phone")
	   private String phone;
	   
	   @Column(name = "email")
	   private String email;
	   
	   @Column(name = "password")
	   private String password;
	   
	   @Column(name = "valid")
	   private boolean isValid = false;
	   
	   @Column(name="MaxDevices",nullable = false)
	   private Integer max = 10;
	   
	   @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	   @JoinColumn(name = "user_id")
	   private Set<Device> devices = new HashSet<Device>();
	   
	   @Column(name="otp")
	   private String otp;
	   /*
	   private static PasswordEncoder passwordEncoder;
	   static {
		   passwordEncoder = new BCryptPasswordEncoder();
	   }
	   */
	   final static Random rand = new Random();
	   public User() {
		   this.pubsubenabled = false;
	   }
	   
	   public User(String name, String password, String email, String phone) {
	      this.name = name;
	      this.phone = phone;
	      this.email = email;
	      //this.password = passwordEncoder.encode(password);
	      uuid = UUID.randomUUID().toString();
	      this.otp = String.valueOf(rand.nextInt(99999));	    
	      this.isIFTTT = false;
	      this.pubsubenabled = false;
	   }
	   
	   public Boolean isIFTTT() {
		return isIFTTT;
	}

	public void setIFTTT(Boolean isIFTTT) {		
		this.isIFTTT = isIFTTT;
		if(this.isIFTTT == null)
			this.isIFTTT = true;
	}

	public boolean checkPassword(String pass) {
		return this.password.equals(pass);
		   //return passwordEncoder.matches(pass, this.password);
	   }
	   public Set<Device> getDevices() {
		      return devices;
	   }
	   public void setDevices(HashSet<Device> devices) {
		   this.devices = devices;
	   }
	   
	   public String getUuid() {
	      return uuid;
	   }
	   public void setUuid(String id) {
		   uuid = id;
	   }
	   
	   public String getEmail() {
		   return email;
	   }
	   public void setEmail(String mail) {
		   email = mail;
	   }
	   
	   public String getPassword() {
		   return password;
	   }
	   public void setPassword(String pass) {
		   //password = passwordEncoder.encode(pass);
		   password = pass;
	   }
	   
	   public String getName() {
	      return name;
	   }
	   public void setName(String n) {
		   name = n;
	   }
	   
	   public String getPhone() {
	      return phone;
	   }
	   public void setPhone(String p) {
		   phone = p;
	   }
	   
		public boolean isValid() {
			return isValid;
		}
		public void setValid(boolean isValid) {
			this.isValid = isValid;
		}

		public String getOtp() {
			return otp;
		}
		public void setOtp(String otp) {
			this.otp = otp;
		}
		
		
		public Integer getMax() {
			return max;
		}

		public void setMax(Integer max) {
			this.max = max;
		}

		public Set<EndpointHits> getHits() {
			return hits;
		}

		public void setHits(Set<EndpointHits> hits) {
			this.hits = hits;
		}
		
		public void setKey(String key) {
			this.key = key;
		}
		public String getKey() {
			return this.key;
		}
		
		@Override
		public String toString() {
			return String.format("User [uuid=%s, name=%s, phone=%s, email=%s, password=%s]", uuid, name, phone, email,
					password);
		}
}