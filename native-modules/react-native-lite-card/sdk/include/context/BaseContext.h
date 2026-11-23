#pragma once
#ifndef __Context__
#define __Context__

#include "GPChannelSDK.h"

#include <string>
#include <vector>
#include <iostream>
#include <memory>

#include "scp03/scp03.hpp"
#include "scp11/scp11c.hpp"
//#include "utility/Singleton.h"


namespace jub {
namespace context {


class BaseContext {
public:
    BaseContext() {
    }
    BaseContext(const scp11_sharedInfo& info,
                const scp11_crt& crt,
                const std::vector<unsigned char>& rk) {
        _scp03.reset();
        _scp11 = scp11c(info, crt, rk);
    }
    virtual ~BaseContext() {
    }

    void clear() {
        _scp03.reset();
        _scp11.clear();
    }

    scp03& getSCP03Instance() {
        return _scp03;
    }
    scp11c& getSCP11cInstance() {
        return _scp11;
    }

private:
    scp03  _scp03;
    scp11c _scp11;
}; // class BaseContext end


} // namespace context end
} // namespace jub end


#endif // #pragma once
