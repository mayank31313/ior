package ai.mayank.iot.tables;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {
	@Id
	   private String id;
		private String pubPass;
		
		public void setPubPass(String f) {
			if(this.pubPass == null)
				this.pubPass = f;
		}
		
		public String getPubPass() {
			return this.pubPass;
		}
		
		private Boolean isIFTTT;		
	   private String uuid;
	   
	   private String key;
	   
	  
	   private boolean pubsubenabled;
	   
	   public void setPubSubEnabled(boolean f) {
		   this.pubsubenabled = f;
	   }
	   public boolean getPubSubEnabled() {
		   return this.pubsubenabled;
	   }
	   
		private Set<EndpointHits> hits;
		
	   public String getId() {
		return id;
	   }
	
		public void setId(String id) {
			this.id = id;
		}
	
	   private String name;
	   private String phone;
	   private String email;	   
	   private String password;	   
	   private boolean isValid = false;	  
	   private Integer max = 10;	   
	   private Set<Device> devices;
	   private String otp;
	   final static Random rand = new Random();
	   public User() {
		   this.pubsubenabled = false;
	   }
	   
	   public User(String name, String email, String phone) {
	      this.name = name;
	      this.phone = phone;
	      this.email = email;
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
	   public void setDevices(Set<Device> devices) {
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