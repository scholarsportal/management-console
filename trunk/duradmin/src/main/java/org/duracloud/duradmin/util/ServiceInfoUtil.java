package org.duracloud.duradmin.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.duracloud.serviceconfig.user.MultiSelectUserConfig;
import org.duracloud.serviceconfig.user.Option;
import org.duracloud.serviceconfig.user.SingleSelectUserConfig;
import org.duracloud.serviceconfig.user.TextUserConfig;
import org.duracloud.serviceconfig.user.UserConfig;


public class ServiceInfoUtil {
    
    /**
     * 
     * @param userConfig
     * @param parameters
     * @return true if value changed
     */
    public static boolean applyValues(TextUserConfig userConfig, Map<String,String> parameters){
        String newValue = parameters.get(userConfig.getName());
        String oldValue = userConfig.getValue();
        userConfig.setValue(newValue);

        return !StringUtils.equals(newValue, oldValue);
    }

    /**
     * 
     * @param userConfig
     * @param parameters
     * @return true if value changed
     */
    public static boolean applyValues(SingleSelectUserConfig userConfig, Map<String,String> parameters){
        String newValue = parameters.get(userConfig.getName());
        String oldValue = userConfig.getSelectedValue();
        userConfig.select(newValue);
        return !StringUtils.equals(newValue, oldValue);
    }


    /**
     * 
     * @param userConfig
     * @param parameters
     * @return true if value changed
     */
    public static boolean applyValues(MultiSelectUserConfig userConfig, Map<String,String> parameters){
        String name = userConfig.getName();
        String oldValue = getValuesAsString(userConfig);
        userConfig.deselectAll();

        for(String key : parameters.keySet()){
            if(key.startsWith(name+"-checkbox-")){
                int index = Integer.valueOf(key.substring(key.lastIndexOf("-")+1));
                userConfig.getOptions().get(index).setSelected(true);
            }
        }
        
        String newValue = getValuesAsString(userConfig);
        
        return !StringUtils.equals(newValue, oldValue);
    }
    
    private static String getValuesAsString(MultiSelectUserConfig uc){
        StringBuffer b = new StringBuffer();
        for(Option o : uc.getOptions()){
            if(o.isSelected()){
                b.append(o.getValue());
            }
        }
        
        return b.toString();
    }

    
    public static void applyValues(List<UserConfig> userConfigs,
                                   Map<String, String> parameters) {
        for(UserConfig userConfig : userConfigs){
            applyValues(userConfig,parameters);
        }
    }

    private static boolean applyValues(UserConfig userConfig,
                                    Map<String, String> parameters) {
        if(userConfig instanceof TextUserConfig){
            return applyValues((TextUserConfig)userConfig, parameters);
        }else if(userConfig instanceof SingleSelectUserConfig){
            return applyValues((SingleSelectUserConfig)userConfig, parameters);
        }else if(userConfig instanceof MultiSelectUserConfig){
            return applyValues((MultiSelectUserConfig)userConfig, parameters);
        }else{
            throw new UnsupportedOperationException(userConfig.getClass().getCanonicalName() + " not recognized.");
        }
    }

}

