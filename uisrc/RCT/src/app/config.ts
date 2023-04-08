export var config = {

    //highlight main menu

    "menuHighlight": {
        "dashboard": "dashboard",
        "usermanagement": "usermanagement",
        "networkconfig": "usermanagement",
        "generalconfig": "usermanagement",
        "audittrail": "usermanagement",
        "ciqupload": "ciqupload",
        "checklist" : "ciqupload",
        "nemapping": "ciqupload",
        "ranconfig": "ciqupload",
        "pregenerate": "ciqupload",
        "negrow": "ciqupload",
        "negrowstatus":"ciqupload",
        "preaudit": "ciqupload",
        "home": "home",
        "runtest": "runtest",
        "uploadScript": "uploadScript",        
        "cmdrulebuilder": "uploadScript",
        "usecasebuilder": "uploadScript",
        "ranatp": "ranatp",       
        "audit": "ranatp",
        "sitedata":"ranatp",
        "workflowmgmt":"workflowmgmt" ,
       /* "eodreports":"ranatp",
        "scheduling":"scheduling",
        "overallreports":"scheduling",
        "reports":"scheduling" ,*/
        "report":"report"

    },
    "showPrograms" : {
        "dashboard": false,
        "usermanagement": false,
        "networkconfig": false,
        "generalconfig": false,
        "audittrail": false,
        "ciqupload": true,
        "checklist" : true,
        "nemapping": true,
        "ranconfig": true,
        "pregenerate": true,
        "negrow": true,
        "negrowstatus":true,
        "preaudit": true,
        "home": false,
        "runtest": true,
        "uploadScript": true,        
        "cmdrulebuilder": true,
        "usecasebuilder": true,
        "ranatp": true,       
        "audit": true,
        "sitedata":true,
        "eodreports":false,
        "scheduling":false,
        "overallreports":false,
        "reports":false,
        "workflowmgmt":true,
        "report":false
    },
    "showDuoConnectionStatus" : {
        "dashboard": false,
        "usermanagement": false,
        "networkconfig": false,
        "generalconfig": false,
        "audittrail": false,
        "ciqupload": false,
        "checklist" : false,
        "nemapping": false,
        "ranconfig": false,
        "pregenerate": false,
        "negrow": true,
        "negrowstatus":true,
        "preaudit": true,
        "home": false,
        "runtest": true,
        "uploadScript": false,        
        "cmdrulebuilder": false,
        "usecasebuilder": false,
        "ranatp": false,       
        "audit": true,
        "sitedata":false,
        "eodreports":false,
        "scheduling":false,
        "overallreports":false,
        "reports":false,
        "workflowmgmt":true,
        "report":false
    },
    "credentials": {
        "mainMenu": {
            "Default User":{
                "dash": false,
                "configuration": true,
                "rules": false,
                "premigration": false,
                "migration": false,
                "postmigration": false,
                "sandr": false,
                "workflowmgmt": false,
                "report":false
            },
            "Super Administrator": {
                "dash": true,
                "configuration": true,
                "rules": true,
                "premigration": true,
                "migration": true,
                "postmigration": true,
                "sandr": true,
                "workflowmgmt": true,
                "report":true
            },
            "Administrator": {
                "dash": true,
                "configuration": true,
                "rules": true,
                "premigration": true,
                "migration": true,
                "postmigration": true,
                "sandr": true,
                "workflowmgmt": true,
                "report":true
            },
            "Commission Manager": {
                "dash": true,
                "configuration": true,
                "rules": true,
                "premigration": true,
                "migration": true,
                "postmigration": true,
                "sandr": true,
                "workflowmgmt": true,
                "report":true
            },
            "Commission Engineer": {
                "dash": true,
                "configuration": false,
                "rules": true,
                "premigration": true,
                "migration": true,
                "postmigration": true,
                "sandr": false,
                "workflowmgmt": true,
                "report":true
            }
        },
        "subMenu": {
            "Default User": {
                "configuration": [
                    {
                        "displayText": "USER MANAGEMENT",
                        "link": "usermanagement",
                        "required": true
                    }
                ]
            },
            "Super Administrator": {
                /*"dash": [
                  {
                    "displayText": "DASHBOARD",
                    "link": "dashboard",
                    "required": true
                  }
                ],*/
                "home": [],
                "configuration": [
                    {
                        "displayText": "USER MANAGEMENT",
                        "link": "usermanagement",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "N/W CONFIG",
                        "link": "networkconfig",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "GENERAL CONFIG",
                        "link": "generalconfig",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "AUDIT TRAIL",
                        "link": "audittrail",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "rules": [
                    {
                        "displayText": "SCRIPT STORE",
                        "link": "uploadScript",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "COMMAND RULE BUILDER",
                        "link": "cmdrulebuilder",
                        "required": true,
                        "requiredIn5G": true
                    },                    
                    {
                        "displayText": "USE CASE BUILDER",
                        "link": "usecasebuilder",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "premigration": [
                    {
                        "displayText": "RETRIEVE CIQ",
                        "link": "ciqupload",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "CHECK LIST",
                        "link": "checklist",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE MAPPING",
                        "link": "nemapping",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "RAN CONFIG",
                        "link": "ranconfig",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "GENERATE",
                        "link": "pregenerate",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE GROW - DEGROW",
                        "link": "negrow",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE STATUS",
                        "link": "negrowstatus",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "PRE AUDIT",
                        "link": "preaudit",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "migration": [
                    {
                        "displayText": "",
                        "link": "runtest",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "postmigration": [
                    {
                        "displayText": "RAN ATP",
                        "link": "ranatp",
                        "required": false,
                        "requiredIn5G": false
                    },                    
                    {
                        "displayText": "AUDIT",
                        "link": "audit",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "SITE REPORT",
                        "link": "sitedata",
                        "required": true,
                        "requiredIn5G": true
                    }/*,
                    {
                        "displayText": "EOD REPORTS",
                        "link": "eodreports",
                        "required": true,
                        "requiredIn5G": true
                    }*/
                ],
                "sandr": [
                    {
                        "displayText": "SCHEDULING",
                        "link": "scheduling",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "OVERALL REPORTS",
                        "link": "overallreports",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": " REPORTS",
                        "link": "reports",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],

            },
            "Administrator": {
                /*"dash": [
                  {
                    "displayText": "DASHBOARD",
                    "link": "dashboard",
                    "required": true
                  }
                ],*/
                "home": [],
                "configuration": [
                    {
                        "displayText": "USER MANAGEMENT",
                        "link": "usermanagement",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "N/W CONFIG",
                        "link": "networkconfig",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "GENERAL CONFIG",
                        "link": "generalconfig",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "AUDIT TRAIL",
                        "link": "audittrail",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "rules": [
                    {
                        "displayText": "SCRIPT STORE",
                        "link": "uploadScript",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "COMMAND RULE BUILDER",
                        "link": "cmdrulebuilder",
                        "required": true,
                        "requiredIn5G": true
                    },                   
                     {
                        "displayText": "USE CASE BUILDER",
                        "link": "usecasebuilder",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "premigration": [
                    {
                        "displayText": "RETRIEVE CIQ",
                        "link": "ciqupload",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "CHECK LIST",
                        "link": "checklist",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE MAPPING",
                        "link": "nemapping",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "RAN CONFIG",
                        "link": "ranconfig",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "GENERATE",
                        "link": "pregenerate",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE GROW - DEGROW",
                        "link": "negrow",
                        "required": true,
                        "requiredIn5G": true
                    }, 
                    {
                        "displayText": "NE STATUS",
                        "link": "negrowstatus",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "PRE AUDIT",
                        "link": "preaudit",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "migration": [
                    {
                        "displayText": "",
                        "link": "runtest",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "postmigration": [
                    {
                        "displayText": "RAN ATP",
                        "link": "ranatp",
                        "required": false,
                        "requiredIn5G": false
                    },                    
                    {
                        "displayText": "AUDIT",
                        "link": "audit",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "SITE REPORT",
                        "link": "sitedata",
                        "required": true,
                        "requiredIn5G": true
                    }/*,
                    {
                        "displayText": "EOD REPORTS",
                        "link": "eodreports",
                        "required": true,
                        "requiredIn5G": true
                    }*/
                ],
                "sandr": [
                    {
                        "displayText": "SCHEDULING",
                        "link": "scheduling",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "OVERALL REPORTS",
                        "link": "overallreports",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": " REPORTS",
                        "link": "reports",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],

            },
            "Commission Manager": {
                /*"dash": [
                  {
                    "displayText": "DASHBOARD",
                    "link": "dashboard",
                    "required": true
                  }
                ],*/
                "home": [],
                "configuration": [
                    {
                        "displayText": "USER MANAGEMENT",
                        "link": "usermanagement",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "N/W CONFIG",
                        "link": "networkconfig",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "GENERAL CONFIG",
                        "link": "generalconfig",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "AUDIT TRAIL",
                        "link": "audittrail",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "rules": [
                    {
                        "displayText": "SCRIPT STORE",
                        "link": "uploadScript",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "COMMAND RULE BUILDER",
                        "link": "cmdrulebuilder",
                        "required": true,
                        "requiredIn5G": true
                    },                    
                     {
                        "displayText": "USE CASE BUILDER",
                        "link": "usecasebuilder",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "premigration": [
                    {
                        "displayText": "RETRIEVE CIQ",
                        "link": "ciqupload",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "CHECK LIST",
                        "link": "checklist",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE MAPPING",
                        "link": "nemapping",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "RAN CONFIG",
                        "link": "ranconfig",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "GENERATE",
                        "link": "pregenerate",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE GROW - DEGROW",
                        "link": "negrow",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE STATUS",
                        "link": "negrowstatus",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "PRE AUDIT",
                        "link": "preaudit",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "migration": [
                    {
                        "displayText": "",
                        "link": "runtest",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "postmigration": [
                    {
                        "displayText": "RAN ATP",
                        "link": "ranatp",
                        "required": false,
                        "requiredIn5G": false
                    },                    
                    {
                        "displayText": "AUDIT",
                        "link": "audit",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "SITE REPORT",
                        "link": "sitedata",
                        "required": true,
                        "requiredIn5G": true
                    },
                ],
                "sandr": [
                  /* {
                        "displayText": "SCHEDULING",
                        "link": "scheduling",
                        "required": true,
                        "requiredIn5G": true
                    },*/
                    {
                        "displayText": "OVERALL REPORTS",
                        "link": "overallreports",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": " REPORTS",
                        "link": "reports",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
            },
            "Commission Engineer": {
                /*"dash": [
                  {
                    "displayText": "DASHBOARD",
                    "link": "dashboard",
                    "required": true
                  }
                ],*/
                "home": [],
                "configuration": [
                    {
                        "displayText": "USER MANAGEMENT",
                        "link": "usermanagement",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "N/W CONFIG",
                        "link": "networkconfig",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "GENERAL CONFIG",
                        "link": "generalconfig",
                        "required": true,
                        "requiredIn5G": true
                    }, {
                        "displayText": "AUDIT TRAIL",
                        "link": "audittrail",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "rules": [
                    {
                        "displayText": "SCRIPT STORE",
                        "link": "uploadScript",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "COMMAND RULE BUILDER",
                        "link": "cmdrulebuilder",
                        "required": true,
                        "requiredIn5G": true
                    },                   
                     {
                        "displayText": "USE CASE BUILDER",
                        "link": "usecasebuilder",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "premigration": [
                    {
                        "displayText": "CIQ UPLOAD",
                        "link": "ciqupload",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "CHECK LIST",
                        "link": "checklist",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE MAPPING",
                        "link": "nemapping",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "RAN CONFIG",
                        "link": "ranconfig",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "GENERATE",
                        "link": "pregenerate",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE GROW - DEGROW",
                        "link": "negrow",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "NE STATUS",
                        "link": "negrowstatus",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "PRE AUDIT",
                        "link": "preaudit",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "migration": [
                    {
                        "displayText": "",
                        "link": "runtest",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
                "postmigration": [
                    {
                        "displayText": "RAN ATP",
                        "link": "ranatp",
                        "required": false,
                        "requiredIn5G": false
                    },                    
                    {
                        "displayText": "AUDIT",
                        "link": "audit",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "SITE REPORT",
                        "link": "sitedata",
                        "required": true,
                        "requiredIn5G": true
                    }/*,
                    {
                        "displayText": "EOD REPORTS",
                        "link": "eodreports",
                        "required": true,
                        "requiredIn5G": true
                    }*/
                ],
                "sandr": [
                    {
                        "displayText": "SCHEDULING",
                        "link": "scheduling",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": "OVERALL REPORTS",
                        "link": "overallreports",
                        "required": true,
                        "requiredIn5G": true
                    },
                    {
                        "displayText": " REPORTS",
                        "link": "reports",
                        "required": true,
                        "requiredIn5G": true
                    }
                ],
            }
        }
    }
}
