#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <inttypes.h>
#include <stdbool.h>

#include "sysrepo.h"
#include "sysrepo/values.h"
#include "sysrepo/xpath.h"
#include "sysrepo.h"


//---------------------------Derived from application_changes_example.c and oper_data_example.c
//------------------------------------------------------------------------------------------------------------------------
//---------------------------Make sure you start "sudo ./application_example ietf-interfaces"  BEFORE running this program


#define XPATH_MAX_LEN 100

volatile int exit_application = 0;
volatile sr_session_ctx_t *session = NULL;

volatile bool eth0_is_enabled = true;
volatile bool eth1_is_enabled = true;

const char *eth0_xpath = "/ietf-interfaces:interfaces/interface[name='eth0']/enabled";
const char *eth1_xpath = "/ietf-interfaces:interfaces/interface[name='eth1']/enabled";

static void
print_change(sr_change_oper_t op, sr_val_t *old_val, sr_val_t *new_val) {
    switch(op) {
    case SR_OP_CREATED:
    case SR_OP_DELETED:
    case SR_OP_MOVED:
    	break;
    case SR_OP_MODIFIED:
        if (NULL != old_val && NULL != new_val) {
           printf("MODIFIED: ");
           printf("xpath old %s  value %d ", old_val->xpath, old_val->data.bool_val);
           printf("xpath new %s  value %d ", new_val->xpath, new_val->data.bool_val);

           int i = strcmp(eth0_xpath, new_val->xpath);
           if(i == 0){
        	   eth0_is_enabled = new_val->data.bool_val;
           }
           i = strcmp(eth1_xpath, new_val->xpath);
           if(i == 0){
               eth1_is_enabled = new_val->data.bool_val;
           }

        }
        break;
    }
}

static void
print_current_config(sr_session_ctx_t *session, const char *module_name)
{
    sr_val_t *values = NULL;
    size_t count = 0;
    int rc = SR_ERR_OK;
    char select_xpath[XPATH_MAX_LEN];
    snprintf(select_xpath, XPATH_MAX_LEN, "/%s:*//*", module_name);

    rc = sr_get_items(session, select_xpath, &values, &count);
    if (SR_ERR_OK != rc) {
        printf("Error by sr_get_items: %s", sr_strerror(rc));
        return;
    }
    for (size_t i = 0; i < count; i++){
        sr_print_val(&values[i]);
    }
    sr_free_values(values, count);
}

const char *
ev_to_str(sr_notif_event_t ev) {
    switch (ev) {
    case SR_EV_VERIFY:
        return "verify";
    case SR_EV_APPLY:
        return "apply";
    case SR_EV_ABORT:
    default:
        return "abort";
    }
}

static int
module_change_cb(sr_session_ctx_t *session, const char *module_name, sr_notif_event_t event, void *private_ctx)
{
    sr_change_iter_t *it = NULL;
    int rc = SR_ERR_OK;
    sr_change_oper_t oper;
    sr_val_t *old_value = NULL;
    sr_val_t *new_value = NULL;
    char change_path[XPATH_MAX_LEN] = {0,};

    printf("\n\n ========== Notification  %s =============================================", ev_to_str(event));
    printf("\n\n ========== CHANGES: =============================================\n\n");
    snprintf(change_path, XPATH_MAX_LEN, "/%s:*", module_name);

    rc = sr_get_changes_iter(session, change_path , &it);
    if (SR_ERR_OK != rc) {
        printf("Get changes iter failed for xpath %s", change_path);
        goto cleanup;
    }

    while (SR_ERR_OK == (rc = sr_get_change_next(session, it, &oper, &old_value, &new_value))) {
        print_change(oper, old_value, new_value);
        sr_free_val(old_value);
        sr_free_val(new_value);
    }
    printf("\n\n ========== END OF CHANGES =======================================\n\n");

cleanup:
    sr_free_change_iter(it);

    return SR_ERR_OK;
}


static int data_provider_cb(const char *xpath, sr_val_t **values, size_t *values_cnt, uint64_t request_id, const char *original_xpath, void *private_ctx)
{
    sr_val_t *v = NULL;
    sr_xpath_ctx_t xp_ctx = {0};
    int rc = SR_ERR_OK;

    if (sr_xpath_node_name_eq(xpath, "interface")) {

    	 /* allocate space for data to return */
    	rc = sr_new_values(4, &v);
    	if (SR_ERR_OK != rc) {
    		return rc;
    	}

		sr_val_set_xpath(&v[0], "/ietf-interfaces:interfaces-state/interface[name='eth0']/type");
		sr_val_set_str_data(&v[0], SR_IDENTITYREF_T, "ethernetCsmacd");
		sr_val_set_xpath(&v[1], "/ietf-interfaces:interfaces-state/interface[name='eth0']/oper-status");
		if(eth0_is_enabled == true)
			sr_val_set_str_data(&v[1], SR_ENUM_T, "up");
		else
			sr_val_set_str_data(&v[1], SR_ENUM_T, "down");

		sr_val_set_xpath(&v[2], "/ietf-interfaces:interfaces-state/interface[name='eth1']/type");
		sr_val_set_str_data(&v[2], SR_IDENTITYREF_T, "iana-if-type:ethernetCsmacd");
		sr_val_set_xpath(&v[3], "/ietf-interfaces:interfaces-state/interface[name='eth1']/oper-status");
		if(eth1_is_enabled == true)
			sr_val_set_str_data(&v[3], SR_ENUM_T, "up");
		else
			sr_val_set_str_data(&v[3], SR_ENUM_T, "down");

        *values = v;
        *values_cnt = 4;
    } else if (sr_xpath_node_name_eq(xpath, "statistics")) {
    	/* statistics not implemented in this example */
    	 *values = NULL;
    	 values_cnt = 0;
    } else {
        /* ipv4 and ipv6 nested containers not implemented in this example */
        *values = NULL;
        values_cnt = 0;
    }

    return SR_ERR_OK;
}


static void sigint_handler(int signum)
{
    exit_application = 1;
}


static int data_provider(sr_session_ctx_t *session)
{

	char *module_name = "ietf-interfaces";

	sr_subscription_ctx_t *subscriptionConfig = NULL;
	int rc = SR_ERR_OK;

	printf("\n\n ========== READING STARTUP CONFIG: ==========\n\n");
	print_current_config(session, module_name);

	/* subscribe for changes in running config */
	rc = sr_module_change_subscribe(session, module_name, module_change_cb, NULL, 0, SR_SUBSCR_DEFAULT, &subscriptionConfig);
	if (SR_ERR_OK != rc) {
	    fprintf(stderr, "Error by sr_module_change_subscribe: %s\n", sr_strerror(rc));
	    goto cleanup;
	}
	printf("\n\n ========== STARTUP CONFIG APPLIED AS RUNNING ==========\n\n");

	sr_subscription_ctx_t *subscriptionState = NULL;

    /* subscribe for providing operational data */
    rc = sr_dp_get_items_subscribe(session, "/ietf-interfaces:interfaces-state", data_provider_cb, NULL, SR_SUBSCR_DEFAULT, &subscriptionState);
    if (SR_ERR_OK != rc) {
        fprintf(stderr, "Error by sr_dp_get_items_subscribe: %s\n", sr_strerror(rc));
        goto cleanup;
    }

    printf("\n\n ========== SUBSCRIBED FOR PROVIDING OPER DATA ==========\n\n");


    /* loop until ctrl-c is pressed / SIGINT is received */
    signal(SIGINT, sigint_handler);
    signal(SIGPIPE, SIG_IGN);
    while (!exit_application) {
        sleep(1000);  /* or do some more useful work... */
    }

    printf("Application exit requested, exiting.\n");

cleanup:
    if (NULL != subscriptionConfig) {
        sr_unsubscribe(session, subscriptionConfig);
    }
    if (NULL != subscriptionState) {
            sr_unsubscribe(session, subscriptionState);
        }
    return rc;
}


int main(int argc, char **argv)
{
    sr_conn_ctx_t *connection = NULL;
//    sr_session_ctx_t *session = NULL;
    int rc = SR_ERR_OK;

    /* connect to sysrepo */
    rc = sr_connect("example_application", SR_CONN_DEFAULT, &connection);
    if (SR_ERR_OK != rc) {
        fprintf(stderr, "Error by sr_connect: %s\n", sr_strerror(rc));
        goto cleanup;
    }

    /* start session */
    rc = sr_session_start(connection, SR_DS_RUNNING, SR_SESS_DEFAULT, &session);
    if (SR_ERR_OK != rc) {
        fprintf(stderr, "Error by sr_session_start: %s\n", sr_strerror(rc));
        goto cleanup;
    }

    /* run as a data provider */
    printf("This application will be a data provider for state data of ietf-interfaces.\n");
    printf("Run the same executable with one (any) argument to request some data.\n");
    rc = data_provider(session);

cleanup:
    if (NULL != session) {
        sr_session_stop(session);
    }
    if (NULL != connection) {
        sr_disconnect(connection);
    }
    return rc;
}
