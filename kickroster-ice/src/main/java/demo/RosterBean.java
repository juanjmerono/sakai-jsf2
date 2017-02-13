package demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icefaces.ace.model.chart.SectorSeries;
import org.icefaces.ace.model.chart.SectorSeries.SectorType;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

public class RosterBean {
	
	private static Log log = LogFactory.getLog(RosterBean.class);
	
	protected String group;
	public String getGroup() { return group; }
	public void setGroup(String group) { this.group = group; } 

	protected UserDirectoryService userDirectoryService;
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) { this.userDirectoryService = userDirectoryService; }
	
	protected SiteService siteService;
	public void setSiteService(SiteService siteService) { this.siteService = siteService; }
	
	protected ToolManager toolManager;
	public void setToolManager(ToolManager toolManager) { this.toolManager = toolManager; }
	
	public String getVersion() {
		return FacesContext.class.getPackage().getImplementationVersion() + " IceFaces: 4.1.1";
	}
	
    public List<SectorSeries> getPieData() {
		List<SectorSeries> pieData = new ArrayList<SectorSeries>();
		try {
			Map<String,Integer> groups = new HashMap<String,Integer>();
			Site currentSite = siteService.getSite(toolManager.getCurrentPlacement().getContext());
			Collection<Member> currentMembers = currentSite.getMembers();
			for (Member m:currentMembers) {
				for (Group g:currentSite.getGroupsWithMember(m.getUserId())) {
					Integer i = groups.get(g.getTitle());
					if (i==null) i = 0;
					groups.put(g.getTitle(), i++);
				}
			}
			SectorSeries ss = new SectorSeries();
			for (String key:groups.keySet()) {
				ss.add(key,groups.get(key));
			}
	        ss.setType(SectorType.PIE);
	        ss.setShowDataLabels(true);
	        ss.setSliceMargin(4);
			pieData.add(ss);
		} catch (Exception ex) {
			log.error("Error getting members.",ex);
		}
        return pieData;
    }

	public List<Participant> getDataModel() {
		List<Participant> users = new ArrayList<Participant>();
		try {
			Site currentSite = siteService.getSite(toolManager.getCurrentPlacement().getContext());
			if (group==null||group.equals("-")) {
				Collection<Member> currentMembers = currentSite.getMembers();
				for (Member m:currentMembers) {
					StringBuffer groups = new StringBuffer("");
					for (Group g:currentSite.getGroupsWithMember(m.getUserId())) {
						groups.append(g.getTitle() + " ");
					}
					users.add(new Participant(userDirectoryService.getUser(m.getUserId()),m.getRole().getId(),groups.toString()));
				}
			} else {
				Set<String> groups = new HashSet<String>();
				groups.add(group);
				Group grp = currentSite.getGroup(group);
				Collection<String> members = currentSite.getMembersInGroups(groups);
				for (String m:members) {
					Member member = currentSite.getMember(m);
					users.add(new Participant(userDirectoryService.getUser(m),member.getRole().getId(),grp.getTitle()));
				}
			}
		} catch (Exception ex) {
			log.error("Error getting members.",ex);
		}
		return users;
	}
	
    public List<SelectItem> getGroupOptions() {
    	List<SelectItem> items = new ArrayList<SelectItem>();
		items.add(new SelectItem("-","-- Group --"));
		if (siteService!=null && toolManager!=null) {
	    	try {
	    		Collection<Group> groups = siteService.getSite(toolManager.getCurrentPlacement().getContext()).getGroups();
	    		for (Group g:groups) {
	    			items.add(new SelectItem(g.getId(),g.getTitle()));
	    		}
	    	} catch (IdUnusedException ex) {
	    		log.error("Error getting groups.",ex);
	    	}
		}
        return items;  
    }  	
    
    public void save(ActionEvent actionEvent) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		context.addMessage(null, new FacesMessage("Successful", "Hello " + group));
		context.addMessage(null, new FacesMessage("Second Message", "Additional Info Here..."));
	}    
    
    public class Participant {
    	
    	public User user;
    	public String roleTitle;
    	public String groupsString;
    	
    	public Participant(User user, String roleTitle, String groupsString) {
    		this.user = user;
    		this.roleTitle = roleTitle;
    		this.groupsString = groupsString;
    	}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public String getRoleTitle() {
			return roleTitle;
		}

		public void setRoleTitle(String roleTitle) {
			this.roleTitle = roleTitle;
		}

		public String getGroupsString() {
			return groupsString;
		}

		public void setGroupsString(String groupsString) {
			this.groupsString = groupsString;
		}

    }

}
