package demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.VersionBean;
import org.richfaces.model.selection.Selection;
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
	private String sortMode = "multi";
	private String selectionMode = "multi";
	private String tableState;
	
	private Selection selection;

	/* Sakai Services */
	protected SiteService siteService;
	protected ToolManager toolManager;
	protected UserDirectoryService userDirectoryService;

	private SelectItem[] roleOptions = new SelectItem[]{new SelectItem("","Select"),new SelectItem("access","access"),new SelectItem("maintain","maintain"),new SelectItem("Instructor","Instructor"),new SelectItem("Student","Student")};
	
	public String getGroup() { return group; }
	public void setGroup(String group) { this.group = group; } 

	public void setUserDirectoryService(UserDirectoryService userDirectoryService) { this.userDirectoryService = userDirectoryService; }
	public void setSiteService(SiteService siteService) { this.siteService = siteService; }
	public void setToolManager(ToolManager toolManager) { this.toolManager = toolManager; }
	
	public String getVersion() {
		return FacesContext.class.getPackage().getImplementationVersion() + " RichFaces: " + VersionBean._version._versionInfo + " PrimeFaces: 1.1";
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
    	try {
    		Collection<Group> groups = siteService.getSite(toolManager.getCurrentPlacement().getContext()).getGroups();
    		for (Group g:groups) {
    			items.add(new SelectItem(g.getId(),g.getTitle()));
    		}
    	} catch (Exception ex) {
    		log.error("Error getting groups.",ex);
    	}
        return items;  
    }  	
    
	public String getSortMode() { return sortMode; }
	public void setSortMode(String sortMode) { this.sortMode = sortMode; }
	
	public String getSelectionMode() { return selectionMode; }
	public void setSelectionMode(String selectionMode) { this.selectionMode = selectionMode; }

	public String getTableState() { return tableState; }
	public void setTableState(String tableState) { this.tableState = tableState; }
	
	public Selection getSelection() { return selection; }
	public void setSelection(Selection selection) { this.selection = selection; }
	
	public List<Participant> getSelectedItems() { 
		List<Participant> selected = new ArrayList<Participant>();
		if (selection!=null) {
			List<Participant> data= getDataModel();
			Iterator<Object> iterator = selection.getKeys();
			while (iterator.hasNext()) {
				selected.add(data.get((Integer)iterator.next()));
			}
		}
		return selected;
	}
	public void takeSelection() { log.info("TakeSelection " + selection.getKeys()); }
	
    public SelectItem[] getRoleOptions() {  
        return roleOptions;  
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
