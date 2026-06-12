#!/bin/bash

# SNI Direct IP Connection Verification Tool
# This script captures network traffic to verify that:
# 1. Direct IP connection is established
# 2. SNI (Server Name Indication) is correctly sent during TLS handshake

# Configuration
TARGET_IP="104.18.31.39"
TARGET_PORT="443"
SNI_HOSTNAME="wallet.onekeytest.com"

echo "🔍 SNI Direct IP Connection Verification Tool"
echo "=============================================="
echo ""
echo "Target IP:   $TARGET_IP:$TARGET_PORT"
echo "SNI Lookup:  $SNI_HOSTNAME"
echo ""
echo "⚠️  Prerequisites:"
echo "  1. Your application is running"
echo "  2. Ready to trigger the 'Direct IP Connection' feature"
echo "  3. sudo permission is required for packet capture"
echo ""

# Check for sudo permission early
if ! sudo -v &>/dev/null; then
    echo "❌ Error: sudo permission is required to run tcpdump"
    echo "💡 Please run this script with appropriate permissions"
    exit 1
fi

echo "Press Enter to start monitoring..."
read

echo ""
echo "🎯 Packet capture started... (Press Ctrl+C to stop)"
echo "=============================================="
echo ""

# Packet counter
count=0

# Capture and analyze packets
# Using -X for hex+ASCII output to better capture binary TLS data
# Using -s 0 to capture full packets (SNI is in early handshake)
sudo tcpdump -i any -n -X -s 0 "host $TARGET_IP and port $TARGET_PORT" 2>/dev/null | while read -r line; do
    # Detect TCP connection initiation (SYN)
    if [[ $line == *"Flags [S]"* ]] && [[ $line == *"$TARGET_IP.$TARGET_PORT"* ]]; then
        echo "✅ [TCP] Connection initiated to $TARGET_IP:$TARGET_PORT"
        echo ""
    fi

    # Detect TCP connection response (SYN-ACK)
    if [[ $line == *"Flags [S.]"* ]] && [[ $line == *"$TARGET_IP.$TARGET_PORT"* ]]; then
        echo "✅ [TCP] Server acknowledged connection"
        echo ""
    fi

    # Detect SNI field in TLS handshake (both ASCII and hex patterns)
    if [[ $line == *"$SNI_HOSTNAME"* ]]; then
        count=$((count + 1))
        echo ""
        echo "🎉 [TLS] SNI detected (#$count): $SNI_HOSTNAME"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "   ✓ Direct IP Connection + SNI VERIFIED!"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
    fi

    # Display packet summary with timestamps (reduced verbosity)
    if [[ $line =~ ^[0-9]{2}:[0-9]{2}:[0-9]{2} ]]; then
        if [[ $line == *"> $TARGET_IP.$TARGET_PORT"* ]]; then
            # Only show initial request packets to reduce noise
            if [[ $line == *"Flags [P.]"* ]] || [[ $line == *"Flags [S]"* ]]; then
                echo "→ [SENT] $line"
            fi
        elif [[ $line == *"$TARGET_IP.$TARGET_PORT >"* ]]; then
            # Only show response packets with data
            if [[ $line == *"Flags [P.]"* ]] || [[ $line == *"Flags [S.]"* ]]; then
                echo "← [RECV] $line"
            fi
        fi
    fi
done
