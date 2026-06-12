#!/bin/bash

# 配置要屏蔽的 IP 列表
BLOCKED_IPS=(
  # "216.19.4.106"
  "104.18.31.39"
  # 在这里添加更多 IP
)

# pf anchor 文件路径
PF_ANCHOR_FILE="/etc/pf.anchors/block_ips"
PF_CONF="/etc/pf.conf"

# 检查是否有 sudo 权限
if [ "$EUID" -ne 0 ]; then
  echo "❌ 需要 sudo 权限运行此脚本"
  echo "请使用: sudo $0"
  exit 1
fi

echo "📝 正在生成 pf 规则..."

# 生成规则文件
> "$PF_ANCHOR_FILE"  # 清空文件

for ip in "${BLOCKED_IPS[@]}"; do
  echo "block drop out quick from any to $ip" >> "$PF_ANCHOR_FILE"
  echo "  ✓ 添加屏蔽规则: $ip"
done

echo ""
echo "📋 生成的规则文件内容:"
cat "$PF_ANCHOR_FILE"

echo ""
echo "🔄 重新加载 pf 配置..."
pfctl -f "$PF_CONF" 2>&1 | grep -v "Use of -f option"

echo ""
echo "✅ 当前生效的规则:"
pfctl -a block_ips -s rules 2>&1 | grep -v "ALTQ"

echo ""
echo "🎉 完成！已屏蔽 ${#BLOCKED_IPS[@]} 个 IP"
