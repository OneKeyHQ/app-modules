//
//  getgateway.c
//
//  This is pulled directly from https://stackoverflow.com/a/29440193/1120802
//

#include <stdio.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <sys/sysctl.h>
#include "getgateway.h"

#include "TargetConditionals.h"
#if TARGET_IPHONE_SIMULATOR
#define TypeEN    "en1"
#else
#define TypeEN    "en0"
#endif

#include <net/if.h>

// Inline route.h definitions (not available in iOS SDK)
#define RTF_GATEWAY   0x2
#define NET_RT_FLAGS  2
#define RTAX_MAX      8
#define RTAX_DST      0
#define RTAX_GATEWAY  1
#define RTA_DST       0x1
#define RTA_GATEWAY   0x2

struct rt_msghdr {
    u_short rtm_msglen;
    u_char  rtm_version;
    u_char  rtm_type;
    u_short rtm_index;
    int     rtm_flags;
    int     rtm_addrs;
    pid_t   rtm_pid;
    int     rtm_seq;
    int     rtm_errno;
    int     rtm_use;
    u_long  rtm_inits;
    struct  rt_metrics {
        u_long rmx_locks;
        u_long rmx_mtu;
        u_long rmx_hopcount;
        u_long rmx_expire;
        u_long rmx_recvpipe;
        u_long rmx_sendpipe;
        u_long rmx_ssthresh;
        u_long rmx_rtt;
        u_long rmx_rttvar;
        u_long rmx_pksent;
        u_long rmx_filler[4];
    } rtm_rmx;
};
#include <string.h>

#define CTL_NET         4               /* network, see socket.h */

#if defined(BSD) || defined(__APPLE__)

#define ROUNDUP(a) \
((a) > 0 ? (1 + (((a) - 1) | (sizeof(long) - 1))) : sizeof(long))

int getdefaultgateway(in_addr_t *addr)
{
    int mib[] = {CTL_NET, PF_ROUTE, 0, AF_INET,
        NET_RT_FLAGS, RTF_GATEWAY};
    size_t l;
    char *buf, *p;
    struct rt_msghdr *rt;
    struct sockaddr *sa;
    struct sockaddr *sa_tab[RTAX_MAX];
    int i;
    int r = -1;
    if (sysctl(mib, sizeof(mib) / sizeof(int), 0, &l, 0, 0) < 0) {
        return -1;
    }
    if (l > 0) {
        buf = malloc(l);
        if (sysctl(mib, sizeof(mib) / sizeof(int), buf, &l, 0, 0) < 0) {
            free(buf);
            return -1;
        }
        for (p = buf; p < buf + l; p += rt->rtm_msglen) {
            rt = (struct rt_msghdr *)p;
            sa = (struct sockaddr *)(rt + 1);
            for (i = 0; i < RTAX_MAX; i++) {
                if (rt->rtm_addrs & (1 << i)) {
                    sa_tab[i] = sa;
                    sa = (struct sockaddr *)((char *)sa + ROUNDUP(sa->sa_len));
                } else {
                    sa_tab[i] = NULL;
                }
            }

            if (((rt->rtm_addrs & (RTA_DST | RTA_GATEWAY)) == (RTA_DST | RTA_GATEWAY))
                && sa_tab[RTAX_DST]->sa_family == AF_INET
                && sa_tab[RTAX_GATEWAY]->sa_family == AF_INET) {

                if (((struct sockaddr_in *)sa_tab[RTAX_DST])->sin_addr.s_addr == 0) {
                    char ifName[128];
                    if_indextoname(rt->rtm_index, ifName);
                    if (strcmp(TypeEN, ifName) == 0) {
                        *addr = ((struct sockaddr_in *)(sa_tab[RTAX_GATEWAY]))->sin_addr.s_addr;
                        r = 0;
                    }
                }
            }
        }
        free(buf);
    }
    return r;
}

#endif
