package org.yafox.muse.assign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yafox.muse.Pallet;
import org.yafox.muse.util.BeanUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class AssignmentBuilder {

    private Assignment assignment = new Assignment();

    private Map<String, AbstractNode> nodeMap = new HashMap<String, AbstractNode>();

    private JsonObject config;
    
    private Pallet pallet;
    
    private Gson gson = new Gson();

    public static Assignment build(JsonObject config, Pallet pallet) throws Exception {
        if (config == null) {
            return null;
        }
        AssignmentBuilder builder = new AssignmentBuilder();
        builder.config = config;
        builder.pallet = pallet;
        builder.build();
        return builder.assignment;
    }
    
    public AssignmentBuilder() {
        super();
        this.nodeMap.put("", assignment);
    }

    public void build() throws Exception {
        Set<String> keys = config.keySet();
        for (String key : keys) {
            AbstractNode node = createNodeIfAbsent(key);
            JsonElement jsonElement = config.get(key);
            Suggestion suggestion = buildSuggestion(jsonElement);
            node.setSuggestion(suggestion);
        }
    }
    
    public Suggestion buildSuggestion(JsonElement jsonElement) throws Exception {
        if (jsonElement.isJsonNull()) {
            return null;
        }
        
        if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            String type = jsonObject.get("type").getAsString();
            Object bean = pallet.getBean(type);
            Object newBean = gson.fromJson(jsonObject, bean.getClass());
            
            BeanUtil.copy(newBean, bean);
            
            return (Suggestion) bean;
        }
        
        if (jsonElement.isJsonPrimitive()) {
            String beanName = jsonElement.getAsString();
            return (Suggestion) pallet.getBean(beanName);
        }
        
        throw new Exception("error in config " + jsonElement.getAsString());
    }

    public AbstractNode createNodeIfAbsent(String path) {
        if (nodeMap.containsKey(path)) {
            return nodeMap.get(path);
        }
        
        String nodeName = nodeName(path);
        String parentPath = parentPath(path);
        
        AbstractNode parent = createNodeIfAbsent(parentPath);
        List<Node> children = parent.getChildren();
        if (children == null) {
            children = new ArrayList<Node>();
            parent.setChildren(children);
        }
        
        AbstractNode node = null;
        
        boolean multiNode = isMultipleNode(nodeName);
        if (multiNode) {
            node = new MultipleNode();
        } else {
            node = new SingletonNode();
        }
        
        node.setName(nodeName);
        children.add(node);
        
        return node;
    }
    
    public String formatPath(String path) {
        path = path.replaceAll("/+", "/");
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }
    
    public String nodeName(String path) {
        int splitIndex = path.lastIndexOf('/');
        if (splitIndex == -1) {
            return path;
        }
        
        return path.substring(splitIndex + 1);
    }
    
    public String parentPath(String path) {
        int splitIndex = path.lastIndexOf('/');
        if (splitIndex == -1) {
            return "";
        }
        return path.substring(0, splitIndex);
    }
    
    public boolean isMultipleNode(String nodeName) {
        return "*".equals(nodeName);
    }
}
