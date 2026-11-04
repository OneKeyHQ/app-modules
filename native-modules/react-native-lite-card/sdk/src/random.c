// OS randomness adapter. Never substitute deterministic randomness in this target.
#include <TrezorCrypto/rand.h>
#include <errno.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>

void random_buffer(uint8_t* buffer, size_t length) {
    int fd;
    do { fd = open("/dev/urandom", O_RDONLY); } while (fd < 0 && errno == EINTR);
    if (fd < 0) abort();
    size_t offset = 0;
    while (offset < length) {
        ssize_t count = read(fd, buffer + offset, length - offset);
        if (count < 0 && errno == EINTR) continue;
        if (count <= 0) { close(fd); abort(); }
        offset += (size_t)count;
    }
    close(fd);
}
uint32_t random32(void) {
    uint32_t value;
    random_buffer((uint8_t*)&value, sizeof(value));
    return value;
}
