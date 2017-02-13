package demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.Member;
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
	
	public List<Participant> getDataModel() {
		List<Participant> users = new ArrayList<Participant>();
		try {
			Site currentSite = siteService.getSite(toolManager.getCurrentPlacement().getContext());
			if (group==null||group.equals("-")) {
				Collection<Member> currentMembers = currentSite.getMembers();
				for (Member m:currentMembers) {
					users.add(new Participant(userDirectoryService.getUser(m.getUserId()),m.getRole().getId(),""));
				}
			} else {
				Set<String> groups = new HashSet<String>();
				groups.add(group);
				Collection<String> members = currentSite.getMembersInGroups(groups);
				for (String m:members) {
					Member member = currentSite.getMember(m);
					users.add(new Participant(userDirectoryService.getUser(m),member.getRole().getId(),group));
				}
			}
		} catch (Exception ex) {
			log.error("Error getting members.");
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
	    	} catch (Exception ex) {
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
    
    class Participant {
    	
    	public User user;
    	public String roleTitle;
    	public String groupsString;
    	
    	public Participant(User user, String roleTitle, String groupsString) {
    		this.user = user;
    		this.roleTitle = roleTitle;
    		this.groupsString = groupsString;
    	}
    	
    }
    
}
