//
//  getgateway.h
//
//  This is pulled directly from https://stackoverflow.com/a/29440193/1120802
//

#ifndef getgateway_h
#define getgateway_h

#include <stdio.h>
#include <netinet/in.h>

#ifdef __cplusplus
extern "C" {
#endif

int getdefaultgateway(in_addr_t *addr);

#ifdef __cplusplus
}
#endif

#endif /* getgateway_h */
