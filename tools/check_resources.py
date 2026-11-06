#!/usr/bin/env python3
"""Check local model and texture references in both resource source sets."""

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = [ROOT / "src" / source / "resources/assets/rosis_shop"
          for source in ["main", "generated"]]


def exists(kind, identifier, suffix):
    return any((root / kind / (identifier + suffix)).is_file() for root in ASSETS)


def references(value, key=None):
    if isinstance(value, dict):
        for name, child in value.items():
            if name == "textures" and isinstance(child, dict):
                for texture in child.values():
                    if isinstance(texture, str) and texture.startswith("rosis_shop:"):
                        yield "textures", texture, ".png"
            else:
                yield from references(child, name)
    elif isinstance(value, list):
        for child in value:
            yield from references(child, key)
    elif isinstance(value, str) and key in {"parent", "model"} and value.startswith("rosis_shop:"):
        yield "models", value, ".json"


def main():
    count = 0
    errors = []
    for root in ASSETS:
        for path in root.rglob("*.json"):
            for kind, identifier, suffix in references(json.loads(path.read_text())):
                count += 1
                if not exists(kind, identifier.split(":", 1)[1], suffix):
                    errors.append(f"{path.relative_to(ROOT)}: missing {identifier}{suffix}")
    if not count:
        raise SystemExit("No resource references found; check asset paths")
    if errors:
        raise SystemExit("\n".join(errors))
    print(f"Passed: {count} local model and texture references resolve.")


if __name__ == "__main__":
    main()
