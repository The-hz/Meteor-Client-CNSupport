import json
import os

chars = set()
for path in ["minecraft_zh_cn.json", "meteor_zh_cn.json"]:
    if os.path.exists(path):
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
            for v in data.values():
                chars.update(v)
        print(f"从 {path} 提取了 {len(chars)} 个字符")

if os.path.exists("gb2312.txt"):
    with open("gb2312.txt", encoding="utf-8") as f:
        chars.update(f.read())
    print(f"加 GB2312 后共 {len(chars)} 个字符")

keep = []
for c in chars:
    cp = ord(c)
    if cp <= 0x7F:          # ASCII
        continue
    if 0xA0 <= cp <= 0x17F: # Latin-1 Supplement
        continue
    if 0x180 <= cp <= 0x24F: # Latin Extended-A/B
        continue
    if 0x370 <= cp <= 0x3FF: # Greek
        continue
    if 0x400 <= cp <= 0x4FF: # Cyrillic
        continue
    keep.append(cp)

keep = sorted(set(keep))
print(f"最终保留 {len(keep)} 个码点（主要是 CJK）")

# 4. 写入 charset.txt（纯文本，UTF-8）
with open("charset.txt", "w", encoding="utf-8") as f:
    f.write("".join(chr(cp) for cp in keep))

print(f"已生成 charset.txt，放在 {os.path.abspath('charset.txt')}")