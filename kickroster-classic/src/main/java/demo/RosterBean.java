package demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.model.selection.Selection;
import org.sakaiproject.api.app.roster.Participant;
import org.sakaiproject.api.app.roster.RosterManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;

public class RosterBean {
	
	private static Log log = LogFactory.getLog(RosterBean.class);
	
	protected String group;
	private String sortMode = "multi";
	private String selectionMode = "multi";
	private String tableState;
	
	private Selection selection;

	/* Sakai Services */
	protected RosterManager rosterManager;
	protected SiteService siteService;
	protected ToolManager toolManager;

	private SelectItem[] roleOptions = new SelectItem[]{new SelectItem("","Select"),new SelectItem("access","access"),new SelectItem("maintain","maintain"),new SelectItem("Instructor","Instructor"),new SelectItem("Student","Student")};
	
	public String getGroup() { return group; }
	public void setGroup(String group) { this.group = group; } 

	public void setRosterManager(RosterManager rosterManager) { this.rosterManager = rosterManager; }
	public void setSiteService(SiteService siteService) { this.siteService = siteService; }
	public void setToolManager(ToolManager toolManager) { this.toolManager = toolManager; }
	
	public List<Participant> getDataModel() {
		List<Participant> list = new ArrayList<Participant>();
		if (rosterManager!=null) list = group==null||group.equals("-")?rosterManager.getRoster():rosterManager.getRoster(group);
		return list;
	}
	
    public List<SelectItem> getGroupOptions() {
    	List<SelectItem> items = new ArrayList<SelectItem>();
		items.add(new SelectItem("-","-- Group --"));
    	try {
    		Collection<Group> groups = siteService.getSite(toolManager.getCurrentPlacement().getContext()).getGroups();
    		for (Group g:groups) {
    			items.add(new SelectItem(g.getId(),g.getTitle()));
    		}
    	} catch (IdUnusedException ex) {
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
    
}
